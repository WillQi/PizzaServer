package io.github.pizzaserver.server.player;

import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.*;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import com.nukkitx.protocol.bedrock.packet.*;
import io.github.pizzaserver.server.ImplServer;
import io.github.pizzaserver.server.entity.ImplHumanEntity;
import io.github.pizzaserver.server.entity.inventory.BaseInventory;
import io.github.pizzaserver.server.entity.inventory.ImplPlayerInventory;
import io.github.pizzaserver.server.level.world.ImplWorld;
import io.github.pizzaserver.server.network.handlers.GamePacketHandler;
import io.github.pizzaserver.server.network.protocol.PlayerSession;
import io.github.pizzaserver.server.network.protocol.ServerProtocol;
import io.github.pizzaserver.server.network.data.LoginData;
import io.github.pizzaserver.server.player.playerdata.PlayerData;
import io.github.pizzaserver.server.player.playerdata.provider.PlayerDataProvider;
import io.github.pizzaserver.api.entity.EntityRegistry;
import io.github.pizzaserver.api.entity.inventory.Inventory;
import io.github.pizzaserver.api.entity.definition.impl.HumanEntityDefinition;
import io.github.pizzaserver.api.event.type.block.BlockStopBreakEvent;
import io.github.pizzaserver.api.event.type.entity.EntityDamageEvent;
import io.github.pizzaserver.api.event.type.player.PlayerLoginEvent;
import io.github.pizzaserver.api.event.type.player.PlayerRespawnEvent;
import io.github.pizzaserver.api.level.data.Difficulty;
import io.github.pizzaserver.api.level.world.World;
import io.github.pizzaserver.api.level.world.data.Dimension;
import io.github.pizzaserver.api.network.protocol.versions.MinecraftVersion;
import io.github.pizzaserver.api.player.AdventureSettings;
import io.github.pizzaserver.api.player.Player;
import io.github.pizzaserver.api.player.PlayerList;
import io.github.pizzaserver.api.entity.data.attributes.Attribute;
import io.github.pizzaserver.api.player.data.Gamemode;
import io.github.pizzaserver.api.utils.Location;
import io.github.pizzaserver.api.utils.TextMessage;
import io.github.pizzaserver.commons.utils.NumberUtils;
import io.github.pizzaserver.api.entity.data.attributes.AttributeType;
import io.github.pizzaserver.api.player.data.Device;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

public class ImplPlayer extends ImplHumanEntity implements Player {

    protected final ImplServer server;
    protected final PlayerSession session;
    protected boolean locallyInitialized;
    protected boolean autoSave = true;

    protected final MinecraftVersion version;
    protected final Device device;
    protected final String xuid;
    protected final UUID uuid;
    protected final String username;
    protected final String languageCode;

    protected final PlayerList playerList = new ImplPlayerList(this);

    protected final PlayerChunkManager chunkManager = new PlayerChunkManager(this);
    protected Dimension dimensionTransferScreen = null;

    protected Inventory openInventory = null;

    protected Gamemode gamemode;
    protected ImplAdventureSettings adventureSettings = new ImplAdventureSettings();

    protected final PlayerBlockBreakingManager breakingManager = new PlayerBlockBreakingManager(this);


    public ImplPlayer(ImplServer server, PlayerSession session, LoginData loginData) {
        super(EntityRegistry.getDefinition(HumanEntityDefinition.ID));
        this.server = server;
        this.session = session;

        this.version = session.getVersion();
        this.device = loginData.getDevice();
        this.xuid = loginData.getXUID();
        this.uuid = loginData.getUUID();
        this.username = loginData.getUsername();
        this.languageCode = loginData.getLanguageCode();
        this.skin = loginData.getSkin();
        this.inventory = new ImplPlayerInventory(this);

        this.setDisplayName(this.getUsername());
        this.physicsEngine.setPositionUpdate(false);

        // Players will die at any health lower than 0.5
        this.getAttribute(AttributeType.HEALTH).setMinimumValue(0.5f);
    }

    /**
     * Initialize the player to be ready to spawn in.
     */
    public void initialize() {
        this.getServer().getScheduler().prepareTask(() -> {
            // Load player data
            PlayerData data;
            try {
                data = this.getSavedData().orElse(null);
            } catch (IOException exception) {
                this.getServer().getLogger().error("Failed to retrieve data of " + this.getUUID(), exception);
                this.disconnect();
                return;
            }
            if (data == null) {
                String defaultWorldName = this.getServer().getConfig().getDefaultWorldName();
                ImplWorld defaultWorld = this.getServer().getLevelManager().getLevelDimension(defaultWorldName, Dimension.OVERWORLD);
                if (defaultWorld == null) {
                    this.disconnect();
                    this.getServer().getLogger().error("Failed to find a world by the name of " + defaultWorldName);
                    return;
                }
                data = defaultWorld.getDefaultPlayerData();
            }

            this.completePlayerInitialization(data);
        }).setAsynchronous(true).schedule();
    }

    /**
     * Applies player data on the main thread and sends remaining packets to spawn player.
     * @param data player data
     */
    private void completePlayerInitialization(PlayerData data) {
        this.getServer().getScheduler().prepareTask(() -> {
            EntityRegistry.getDefinition(HumanEntityDefinition.ID).onCreation(this);

            // Apply player data
            this.setPitch(data.getPitch());
            this.setYaw(data.getYaw());
            this.setHeadYaw(data.getYaw());
            this.setGamemode(data.getGamemode());

            // Get their spawn location
            World world = this.server.getLevelManager().getLevelDimension(data.getLevelName(), data.getDimension());
            Location location;
            if (world == null) { // Was the world deleted? Set it to the default world if so
                String defaultWorldName = this.getServer().getConfig().getDefaultWorldName();
                world = this.getServer().getLevelManager().getLevelDimension(defaultWorldName, Dimension.OVERWORLD);
                location = new Location(world, world.getSpawnCoordinates());
            } else {
                location = new Location(world, data.getPosition());
            }

            PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(this, location, data.getPitch(), data.getYaw());
            this.getServer().getEventManager().call(playerLoginEvent);
            if (playerLoginEvent.isCancelled()) {
                this.disconnect();
                return;
            }

            // Send remaining packets to spawn player
            SyncedPlayerMovementSettings movementSettings = new SyncedPlayerMovementSettings();
            movementSettings.setMovementMode(AuthoritativeMovementMode.CLIENT);
            StartGamePacket startGamePacket = new StartGamePacket();
            startGamePacket.setUniqueEntityId(this.getId());
            startGamePacket.setRuntimeEntityId(this.getId());
            startGamePacket.setPlayerGameType(GameType.from(this.getGamemode().ordinal()));
            startGamePacket.setPlayerPosition(location.toVector3f().add(0, 2, 0));
            startGamePacket.setRotation(Vector2f.from(this.getPitch(), this.getYaw()));
            startGamePacket.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
            startGamePacket.setCustomBiomeName("");
            startGamePacket.setDimensionId(Dimension.OVERWORLD.ordinal());
            startGamePacket.setLevelGameType(GameType.from(Gamemode.SURVIVAL.ordinal()));
            startGamePacket.setDifficulty(Difficulty.PEACEFUL.ordinal());
            startGamePacket.setDefaultSpawn(world.getSpawnCoordinates());
            startGamePacket.setDayCycleStopTime(world.getTime());
            startGamePacket.setEducationProductionId("");
            startGamePacket.setMultiplayerGame(true);
            startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
            startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
            startGamePacket.setCommandsEnabled(true);
            startGamePacket.setTexturePacksRequired(this.getServer().getResourcePackManager().arePacksRequired());
            startGamePacket.setDefaultPlayerPermission(PlayerPermission.MEMBER);
            startGamePacket.setServerChunkTickRange(this.server.getConfig().getChunkRadius());
            startGamePacket.setVanillaVersion(ServerProtocol.GAME_VERSION);
            startGamePacket.setEduSharedUriResource(EduSharedUriResource.EMPTY);
            startGamePacket.setForceExperimentalGameplay(true);
            startGamePacket.setLevelName(world.getLevel().getName());
            startGamePacket.setLevelId(Base64.getEncoder().encodeToString(startGamePacket.getLevelName().getBytes(StandardCharsets.UTF_8)));
            startGamePacket.setGeneratorId(1);
            startGamePacket.setPremiumWorldTemplateId("");
            startGamePacket.setPlayerMovementSettings(movementSettings);
            startGamePacket.setAuthoritativeMovementMode(AuthoritativeMovementMode.CLIENT);
            startGamePacket.setCurrentTick(this.getServer().getTick());
            startGamePacket.setInventoriesServerAuthoritative(true);
            startGamePacket.setBlockPalette(this.getVersion().getCustomBlockPalette());
            startGamePacket.setItemEntries(this.getVersion().getItemEntries());
            startGamePacket.setMultiplayerCorrelationId("");
            startGamePacket.setServerEngine("");
            this.sendPacket(startGamePacket);

            // Send item components for custom items
            ItemComponentPacket itemComponentPacket = new ItemComponentPacket();
            itemComponentPacket.getItems().addAll(this.getVersion().getItemComponents());
            this.sendPacket(itemComponentPacket);

            // TODO: Add creative contents to prevent mobile clients from crashing
            CreativeContentPacket creativeContentPacket = new CreativeContentPacket();
            this.sendPacket(creativeContentPacket);

            BiomeDefinitionListPacket biomeDefinitionPacket = new BiomeDefinitionListPacket();
            biomeDefinitionPacket.setDefinitions(this.getVersion().getBiomeDefinitions());
            this.sendPacket(biomeDefinitionPacket);

            AvailableEntityIdentifiersPacket availableEntityIdentifiersPacket = new AvailableEntityIdentifiersPacket();
            availableEntityIdentifiersPacket.setIdentifiers(this.getVersion().getEntityIdentifiers());
            this.sendPacket(availableEntityIdentifiersPacket);


            // Sent the full player list to this player
            List<PlayerList.Entry> entries = this.getServer().getPlayers().stream()
                    .filter(otherPlayer -> !otherPlayer.isHiddenFrom(this))
                    .map(Player::getPlayerListEntry)
                    .collect(Collectors.toList());
            this.getPlayerList().addEntries(entries);

            location.getWorld().addEntity(this, location.toVector3f());
            this.session.setPacketHandler(new GamePacketHandler(this));

            PlayStatusPacket playStatusPacket = new PlayStatusPacket();
            playStatusPacket.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
            this.sendPacket(playStatusPacket);
        }).schedule();
    }

    @Override
    public MinecraftVersion getVersion() {
        return this.version;
    }

    @Override
    public Device getDevice() {
        return this.device;
    }

    @Override
    public String getXUID() {
        return this.xuid;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getLanguageCode() {
        return this.languageCode;
    }

    @Override
    public boolean isLocallyInitialized() {
        return this.locallyInitialized;
    }

    @Override
    public Gamemode getGamemode() {
        return this.gamemode;
    }

    @Override
    public void setGamemode(Gamemode gamemode) {
        this.gamemode = gamemode;

        if (this.hasSpawned()) {
            SetPlayerGameTypePacket setPlayerGamemodePacket = new SetPlayerGameTypePacket();
            setPlayerGamemodePacket.setGamemode(gamemode.ordinal());
            this.sendPacket(setPlayerGamemodePacket);
            this.updateAdventureSettings();
        }
    }

    @Override
    public boolean inCreativeMode() {
        return this.getGamemode() == Gamemode.CREATIVE;
    }

    @Override
    public boolean isAdventureMode() {
        return this.getGamemode() == Gamemode.ADVENTURE;
    }

    @Override
    public boolean inSurvivalMode() {
        return this.getGamemode() == Gamemode.SURVIVAL;
    }

    /**
     * Updates the adventure settings based off of the current gamemode.
     */
    protected void updateAdventureSettings() {
        AdventureSettings adventureSettings = this.getAdventureSettings();
        adventureSettings.setCanFly(this.getGamemode().equals(Gamemode.CREATIVE));
        if (adventureSettings.isFlying()) {
            adventureSettings.setIsFlying(this.getGamemode().equals(Gamemode.CREATIVE));
        }
        this.setAdventureSettings(adventureSettings);
    }

    @Override
    public AdventureSettings getAdventureSettings() {
        return this.adventureSettings.clone();
    }

    @Override
    public void setAdventureSettings(AdventureSettings adventureSettings) {
        ImplAdventureSettings settings = (ImplAdventureSettings) adventureSettings;
        this.adventureSettings = settings;

        AdventureSettingsPacket adventureSettingsPacket = new AdventureSettingsPacket();
        adventureSettingsPacket.setUniqueEntityId(this.getId());
        adventureSettingsPacket.setPlayerPermission(settings.getPlayerPermission());
        adventureSettingsPacket.setCommandPermission(settings.getCommandPermission());
        adventureSettingsPacket.getSettings().addAll(settings.getSettings());
        this.sendPacket(adventureSettingsPacket);
    }

    public void onLocallyInitialized() {
        this.locallyInitialized = true;

        for (Player player : this.getServer().getPlayers()) {
            if (!this.isHiddenFrom(player) && !player.equals(this)) {
                player.getPlayerList().addEntry(this.getPlayerListEntry());
            }
        }

        this.getChunkManager().onLocallyInitialized();
    }

    public PlayerBlockBreakingManager getBlockBreakingManager() {
        return this.breakingManager;
    }

    public boolean canReach(Vector3i vector3, float maxDistance) {
        return this.canReach(Vector3f.from(vector3.getX(), vector3.getY(), vector3.getZ()), maxDistance);
    }

    public boolean canReach(Vector3f vector3, float maxDistance) {
        Vector3f position = this.getLocation().toVector3f().add(0, this.getEyeHeight(), 0);

        // Distance check
        double distance = position.distance(vector3);
        if (distance > maxDistance) {
            return false;
        }

        // Direction check
        Vector3f playerDirectionVector = this.getDirectionVector();
        Vector3f targetDirectionVector = vector3.sub(this.getLocation().toVector3f().add(0, this.getEyeHeight(), 0)).normalize();

        // Must be in same direction ( > 0) but we allow a little leeway to account for attacking an entity in the same position as you
        return playerDirectionVector.dot(targetDirectionVector) > -1;
    }

    @Override
    public void kill() {
        if (!this.getGamemode().equals(Gamemode.CREATIVE)) {
            super.kill();
        }
    }

    @Override
    public void hurt(float damage) {
        if (!this.getGamemode().equals(Gamemode.CREATIVE)) {
            super.hurt(damage);
        }
    }

    @Override
    public boolean damage(EntityDamageEvent event) {
        if (this.getGamemode().equals(Gamemode.CREATIVE)) {
            return false;
        } else {
            return super.damage(event);
        }
    }

    public void respawn() {
        this.deathAnimationTicks = -1;
        this.fireTicks = 0;
        this.noHitTicks = 0;
        this.lastDamageEvent = null;
        this.setAI(true);
        this.setAirSupplyTicks(this.getMaxAirSupplyTicks());
        this.setSwimming(false);

        Location respawnLocation = this.getSpawn();
        if (respawnLocation.getWorld().getDimension() != this.getWorld().getDimension()) {
            this.setDimensionTransferScreen(respawnLocation.getWorld().getDimension());
        }

        this.setHealth(this.getMaxHealth());
        this.setFoodLevel(20);

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, respawnLocation);
        this.getServer().getEventManager().call(respawnEvent);

        respawnLocation.getWorld().addEntity(this, respawnEvent.getLocation().toVector3f());
        this.teleport(respawnEvent.getLocation());
    }

    public ImplServer getServer() {
        return this.server;
    }

    @Override
    public ImplPlayerInventory getInventory() {
        return (ImplPlayerInventory) this.inventory;
    }

    @Override
    public Optional<Inventory> getOpenInventory() {
        return Optional.ofNullable(this.openInventory);
    }

    @Override
    public boolean closeOpenInventory() {
        Optional<Inventory> openInventory = this.getOpenInventory();
        if (openInventory.isPresent() && !((BaseInventory) openInventory.get()).closeFor(this)) {
            return false;
        } else {
            this.openInventory = null;
            return true;
        }
    }

    @Override
    public boolean openInventory(Inventory inventory) {
        this.closeOpenInventory();
        if (((BaseInventory) inventory).openFor(this)) {
            this.openInventory = inventory;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public PlayerList getPlayerList() {
        return this.playerList;
    }

    /**
     * Fetch the SAVED player data from the {@link PlayerDataProvider} if any exists.
     * @return saved player data
     * @throws IOException if an exception occurred while reading the data
     */
    public Optional<PlayerData> getSavedData() throws IOException {
        return this.getServer().getPlayerProvider()
                .load(this.getUUID());
    }

    @Override
    public boolean save() {
        if (this.hasSpawned()) {
            PlayerData playerData = new PlayerData.Builder()
                    .setLevelName(this.getLevel().getProvider().getFileName())
                    .setDimension(this.getLocation().getWorld().getDimension())
                    .setGamemode(this.getGamemode())
                    .setPosition(this.getLocation().toVector3f())
                    .setPitch(this.getPitch())
                    .setYaw(this.getYaw())
                    .build();
            try {
                this.getServer().getPlayerProvider().save(this.getUUID(), playerData);
                return true;
            } catch (IOException exception) {
                this.getServer().getLogger().error("Failed to save player " + this.getUUID(), exception);
            }
        }
        return false;
    }

    /**
     * Called when the server registers that the player is disconnected.
     * It cleans up data for this player
     */
    public void onDisconnect() {
        if (this.hasSpawned()) {
            this.closeOpenInventory();

            if (this.canAutoSave()) {
                this.getServer().getScheduler().prepareTask(() -> {
                    this.save();
                    this.getServer().getScheduler().prepareTask(this::despawn).schedule();
                }).setAsynchronous(true).schedule();
            } else {
                this.despawn();
            }

            // remove the player from the player list of others
            for (Player player : this.getServer().getPlayers()) {
                player.getPlayerList().removeEntry(this.getPlayerListEntry());
            }
        }
    }

    private void sendAttribute(Attribute attribute) {
        this.sendAttributes(Collections.singleton(attribute));
    }

    private void sendAttributes() {
        this.sendAttributes(this.attributes.getAttributes());
    }

    private void sendAttributes(Set<Attribute> attributes) {
        if (this.hasSpawned()) {
            UpdateAttributesPacket updateAttributesPacket = new UpdateAttributesPacket();
            updateAttributesPacket.setRuntimeEntityId(this.getId());
            updateAttributesPacket.setAttributes(attributes.stream().map(Attribute::serialize).collect(Collectors.toList()));
            this.sendPacket(updateAttributesPacket);
        }
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        this.sendAttribute(this.getAttribute(AttributeType.HEALTH));
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        super.setMaxHealth(maxHealth);
        this.sendAttribute(this.getAttribute(AttributeType.HEALTH));
    }

    @Override
    public void setAbsorption(float absorption) {
        super.setAbsorption(absorption);
        this.sendAttribute(this.getAttribute(AttributeType.ABSORPTION));
    }

    @Override
    public void setMaxAbsorption(float maxAbsorption) {
        super.setMaxAbsorption(maxAbsorption);
        this.sendAttribute(this.getAttribute(AttributeType.ABSORPTION));
    }

    @Override
    public void setMovementSpeed(float movementSpeed) {
        super.setMovementSpeed(movementSpeed);
        this.sendAttribute(this.getAttribute(AttributeType.MOVEMENT_SPEED));
    }

    @Override
    public float getFoodLevel() {
        Attribute attribute = this.getAttribute(AttributeType.FOOD);
        return attribute.getCurrentValue();
    }

    @Override
    public void setFoodLevel(float foodLevel) {
        Attribute attribute = this.getAttribute(AttributeType.FOOD);
        attribute.setCurrentValue(Math.max(attribute.getMinimumValue(), foodLevel));
        this.sendAttribute(attribute);
    }

    @Override
    public float getSaturationLevel() {
        Attribute attribute = this.getAttribute(AttributeType.SATURATION);
        return attribute.getCurrentValue();
    }

    @Override
    public void setSaturationLevel(float saturationLevel) {
        Attribute attribute = this.getAttribute(AttributeType.SATURATION);
        attribute.setCurrentValue(Math.max(attribute.getMinimumValue(), saturationLevel));
        this.sendAttribute(attribute);
    }

    @Override
    public float getExperience() {
        Attribute attribute = this.getAttribute(AttributeType.EXPERIENCE);
        return attribute.getCurrentValue();
    }

    @Override
    public void setExperience(float experience) {
        Attribute attribute = this.getAttribute(AttributeType.EXPERIENCE);
        attribute.setCurrentValue(Math.max(attribute.getMinimumValue(), experience));
        this.sendAttribute(attribute);
    }

    @Override
    public int getExperienceLevel() {
        Attribute attribute = this.getAttribute(AttributeType.EXPERIENCE_LEVEL);
        return (int) attribute.getCurrentValue();
    }

    @Override
    public void setExperienceLevel(int experienceLevel) {
        Attribute attribute = this.getAttribute(AttributeType.EXPERIENCE_LEVEL);
        attribute.setCurrentValue(Math.max(attribute.getMinimumValue(), experienceLevel));
        this.sendAttribute(attribute);
    }

    @Override
    public void teleport(World world, float x, float y, float z) {
        this.teleport(world, x, y, z, world.getDimension());
    }

    @Override
    public void teleport(float x, float y, float z, Dimension transferDimension) {
        this.teleport(this.getWorld(), x, y, z, transferDimension);
    }

    @Override
    public void teleport(Location location, Dimension transferDimension) {
        this.teleport(location.getWorld(), location.getX(), location.getY(), location.getZ(), transferDimension);
    }

    @Override
    public void teleport(World world, float x, float y, float z, Dimension transferDimension) {
        World oldWorld = this.getWorld();

        super.teleport(world, x, y, z);
        MoveEntityAbsolutePacket teleportPacket = new MoveEntityAbsolutePacket();
        teleportPacket.setRuntimeEntityId(this.getId());
        teleportPacket.setPosition(Vector3f.from(x, y + this.getEyeHeight(), z));
        teleportPacket.setRotation(Vector3f.from(this.getPitch(), this.getYaw(), this.getHeadYaw()));
        teleportPacket.setTeleported(true);
        this.sendPacket(teleportPacket);

        if (!oldWorld.getDimension().equals(transferDimension)) {
            this.setDimensionTransferScreen(transferDimension);
        }
    }

    @Override
    public Location getSpawn() {
        if (this.getHome().isPresent()) {
            return new Location(this.getWorld(), this.getHome().get().toLocation().toVector3f().add(0, this.getEyeHeight(), 0));
        } else {
            World world = this.getServer().getLevelManager().getDefaultLevel().getDimension(Dimension.OVERWORLD);
            return new Location(world, world.getSpawnCoordinates().add(0, this.getEyeHeight(), 0));
        }
    }

    /**
     * Returns the current dimension transfer screen being shown to the player.
     * @return dimension transfer screen
     */
    public Optional<Dimension> getDimensionTransferScreen() {
        return Optional.ofNullable(this.dimensionTransferScreen);
    }

    /**
     * Send a dimension change packet.
     * @param dimension dimension to send the transfer screen of
     */
    public void setDimensionTransferScreen(Dimension dimension) {
        this.dimensionTransferScreen = dimension;
        if (dimension != null) {
            ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();
            changeDimensionPacket.setDimension(dimension.ordinal());
            changeDimensionPacket.setPosition(this.getLocation().toVector3f().add(0, this.getEyeHeight(), 0));
            if (!this.isAlive()) {
                changeDimensionPacket.setRespawn(true);
            }
            this.sendPacket(changeDimensionPacket);
            this.getChunkManager().onDimensionTransfer();
        }
    }

    @Override
    public void sendMessage(TextMessage message) {
        TextPacket textPacket = new TextPacket();
        textPacket.setType(message.getType());
        textPacket.setMessage(message.getMessage());
        textPacket.setNeedsTranslation(message.requiresTranslation());
        textPacket.setSourceName(message.getSourceName());
        textPacket.setParameters(message.getParameters());
        textPacket.setXuid(message.getXuid());
        textPacket.setPlatformChatId(message.getPlatformChatId());
        this.sendPacket(textPacket);
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(new TextMessage.Builder()
                .setType(TextPacket.Type.RAW)
                .setMessage(message)
                .build());
    }

    @Override
    public void sendPlayerMessage(Player sender, String message) {
        this.sendMessage(new TextMessage.Builder()
                        .setType(TextPacket.Type.CHAT)
                        .setSourceName(sender.getUsername())
                        .setMessage(message)
                        .setXUID(sender.getXUID())
                        .build());
    }

    public PlayerChunkManager getChunkManager() {
        return this.chunkManager;
    }

    @Override
    public int getChunkRadius() {
        return this.getChunkManager().getChunkRadius();
    }

    @Override
    public void setChunkRadius(int radius) {
        this.getChunkManager().setChunkRadius(radius);
    }

    @Override
    public boolean isConnected() {
        return !this.session.getConnection().isClosed();
    }

    @Override
    public long getPing() {
        return this.session.getConnection().getLatency();
    }

    @Override
    public void sendPacket(BedrockPacket packet) {
        this.session.getConnection().sendPacket(packet);
    }

    @Override
    public void sendPacketImmediately(BedrockPacket packet) {
        this.session.getConnection().sendPacketImmediately(packet);
    }

    @Override
    public void disconnect() {
        this.session.getConnection().disconnect();
    }

    @Override
    public void disconnect(String reason) {
        this.session.getConnection().disconnect(reason);
    }

    @Override
    public void setAutoSave(boolean allowAutoSaving) {
        this.autoSave = allowAutoSaving;
    }

    @Override
    public boolean canAutoSave() {
        return this.autoSave;
    }

    @Override
    public void tick() {
        if (this.metaDataUpdate) {
            SetEntityDataPacket setEntityDataPacket = new SetEntityDataPacket();
            setEntityDataPacket.setRuntimeEntityId(this.getId());
            setEntityDataPacket.getMetadata().putAll(this.getMetaData().serialize());
            this.sendPacket(setEntityDataPacket);
        }

        // Make sure that the block we're breaking is within reach!
        boolean stopBreakingBlock = this.getBlockBreakingManager().getBlock().isPresent()
                && !(this.canReach(this.getBlockBreakingManager().getBlock().get().getLocation().toVector3i(), this.getGamemode().equals(Gamemode.CREATIVE) ? 13 : 7)
                        && this.isAlive()
                        && this.getAdventureSettings().canMine());
        if (stopBreakingBlock) {
            BlockStopBreakEvent blockStopBreakEvent = new BlockStopBreakEvent(this, this.getBlockBreakingManager().getBlock().get());
            this.getServer().getEventManager().call(blockStopBreakEvent);

            this.getBlockBreakingManager().stopBreaking();
        }

        if (!NumberUtils.isNearlyEqual(this.getHealth(), this.getMaxHealth()) && this.getFoodLevel() >= 18 && this.ticks % 80 == 0) {
            this.setHealth(this.getHealth() + 1);
        }

        super.tick();
    }

    @Override
    public void moveTo(float x, float y, float z) {
        Location oldLocation = new Location(this.world, Vector3f.from(this.x, this.y, this.z));
        super.moveTo(x, y, z);

        if (!oldLocation.getChunk().equals(this.getChunk())) {
            this.getChunkManager().onEnterNewChunk(oldLocation);
        }
    }

    @Override
    public void onSpawned() {
        super.onSpawned();
        this.getChunkManager().onSpawned();

        this.getInventory().sendSlots(this);
        this.getMetaData().putFlag(EntityFlag.HAS_GRAVITY, true)
                .putFlag(EntityFlag.BREATHING, true)
                .putFlag(EntityFlag.WALL_CLIMBING, true);
        this.setMetaData(this.getMetaData());
        this.sendAttributes();
        this.updateAdventureSettings();

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.setTime(this.getWorld().getTime());
        this.sendPacket(setTimePacket);
    }

    @Override
    public void onDespawned() {
        super.onDespawned();
        this.getChunkManager().onDespawn();
    }

    @Override
    public void showTo(Player player) {
        if (this.isHiddenFrom(player)) {
            super.showTo(player);
            if (player.hasSpawned()) {  // we only need to add the entry if we were spawned
                player.getPlayerList().addEntry(this.getPlayerListEntry());
            }
        }
    }

    @Override
    public void hideFrom(Player player) {
        if (!this.isHiddenFrom(player)) {
            super.hideFrom(player);
            if (player.hasSpawned()) {  // we only need to remove the entry if we were spawned
                player.getPlayerList().removeEntry(this.getPlayerListEntry());
            }
        }
    }

}
