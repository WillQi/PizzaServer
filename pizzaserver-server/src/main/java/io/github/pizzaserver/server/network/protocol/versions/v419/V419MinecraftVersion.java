package io.github.pizzaserver.server.network.protocol.versions.v419;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nukkitx.nbt.*;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.data.inventory.ComponentItemData;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import com.nukkitx.protocol.bedrock.v419.Bedrock_v419;
import io.github.pizzaserver.api.item.types.components.*;
import io.github.pizzaserver.server.network.protocol.versions.BaseMinecraftVersion;
import io.github.pizzaserver.api.entity.EntityRegistry;
import io.github.pizzaserver.api.entity.definition.EntityDefinition;
import io.github.pizzaserver.api.item.ItemRegistry;
import io.github.pizzaserver.api.item.types.BlockItemType;
import io.github.pizzaserver.api.item.types.ItemType;
import io.github.pizzaserver.api.level.world.blocks.BlockRegistry;
import io.github.pizzaserver.api.level.world.blocks.types.BaseBlockType;
import io.github.pizzaserver.api.network.protocol.utils.MinecraftNamespaceComparator;
import io.github.pizzaserver.commons.utils.Tuple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class V419MinecraftVersion extends BaseMinecraftVersion {

    public static final int PROTOCOL = 419;
    public static final String VERSION = "1.16.100";

    public V419MinecraftVersion() throws IOException {}


    @Override
    public int getProtocol() {
        return PROTOCOL;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public BedrockPacketCodec getPacketCodec() {
        return Bedrock_v419.V419_CODEC;
    }

    @Override
    protected void loadBiomeDefinitions() throws IOException {
        try (NBTInputStream inputStream = NbtUtils.createNetworkReader(this.getProtocolResourceStream("biome_definitions.nbt"))) {
            this.biomesDefinitions = (NbtMap) inputStream.readTag();
        }
    }

    @Override
    protected void loadBlockStates() throws IOException {
        try (NBTInputStream blockStatesNBTStream = NbtUtils.createNetworkReader(this.getProtocolResourceStream("block_states.nbt"))) {
            // keySet returns in ascending rather than descending so we have to reverse it
            SortedMap<String, List<NbtMap>> sortedBlockRuntimeStates =
                    new TreeMap<>(Collections.reverseOrder(MinecraftNamespaceComparator::compare));

            // Parse block states
            NbtMap blockState;
            while ((blockState = (NbtMap) blockStatesNBTStream.readTag()) != null) {
                String name = blockState.getString("name");
                if (!sortedBlockRuntimeStates.containsKey(name)) {
                    sortedBlockRuntimeStates.put(name, new ArrayList<>());
                }

                NbtMap states = blockState.getCompound("states");
                sortedBlockRuntimeStates.get(name).add(states);
            }

            // Add custom block states
            for (BaseBlockType blockType : BlockRegistry.getCustomTypes()) {
                sortedBlockRuntimeStates.put(blockType.getBlockId(), new ArrayList<>(blockType.getBlockStates().keySet()));
            }

            // Block runtime ids are determined by the order of the sorted block runtime states.
            int runtimeId = 0;
            for (String blockId : sortedBlockRuntimeStates.keySet()) {
                for (NbtMap states : sortedBlockRuntimeStates.get(blockId)) {
                    Tuple<String, NbtMap> blockStateLookupKey = new Tuple<>(blockId, states);
                    if (!GLOBAL_BLOCK_STATES.containsKey(blockStateLookupKey)) {
                        GLOBAL_BLOCK_STATES.put(blockStateLookupKey, GLOBAL_BLOCK_STATES.size());
                    }
                    int blockStateLookupId = GLOBAL_BLOCK_STATES.get(blockStateLookupKey);

                    this.blockStates.put(blockStateLookupId, runtimeId++);
                }
            }
        }
    }

    @Override
    protected void loadRuntimeItems() throws IOException {
        try (Reader itemStatesReader = new InputStreamReader(this.getProtocolResourceStream("runtime_item_states.json"))) {
            JsonArray jsonItemStates = GSON.fromJson(itemStatesReader, JsonArray.class);

            int customItemIdStart = 0;  // Custom items can be assigned any id as long as it does not conflict with an existing item

            // Register Vanilla items
            for (JsonElement element : jsonItemStates) {
                JsonObject itemState = element.getAsJsonObject();

                String itemId = itemState.get("name").getAsString();
                int runtimeId = itemState.get("id").getAsInt();
                customItemIdStart = Math.max(customItemIdStart, runtimeId + 1);

                this.itemRuntimeIds.put(itemId, runtimeId);
                this.itemEntries.add(new StartGamePacket.ItemEntry(itemId, (short) runtimeId, false));
            }
            this.itemRuntimeIds.put("minecraft:air", 0);    // A void item is equal to 0 and this reduces data sent over the network

            // Register custom items
            for (ItemType itemType : ItemRegistry.getCustomTypes()) {
                if (!(itemType instanceof BlockItemType)) { // We register item representations of custom blocks later
                    int runtimeId = customItemIdStart++;
                    this.itemRuntimeIds.put(itemType.getItemId(), runtimeId);
                    this.itemEntries.add(new StartGamePacket.ItemEntry(itemType.getItemId(), (short) runtimeId, true));
                }
            }

            //Register custom block items
            int customBlockIdStart = 1000;
            // Block item runtime ids are decided by the order they are sent via the StartGamePacket in the block properties
            // Block properties are sent sorted by their namespace according to Minecraft's namespace sorting.
            // So we will sort it the same way here
            SortedSet<BaseBlockType> sortedCustomBlockTypes =
                    new TreeSet<>((blockTypeA, blockTypeB) -> MinecraftNamespaceComparator.compare(blockTypeA.getBlockId(), blockTypeB.getBlockId()));
            sortedCustomBlockTypes.addAll(BlockRegistry.getCustomTypes());
            for (BaseBlockType customBlockType : sortedCustomBlockTypes) {
                this.itemRuntimeIds.put(customBlockType.getBlockId(), 255 - customBlockIdStart++);  // (255 - index) = item runtime id
            }
        }
    }

    @Override
    protected void loadEntitiesNBT() {
        int rId = 0;    // TODO: what is the purpose of this?
        NbtList<NbtMap> entities = new NbtList<>(NbtType.COMPOUND);
        for (EntityDefinition definition : EntityRegistry.getDefinitions()) {
            entities.add(NbtMap.builder()
                    .putString("bid", "")
                    .putBoolean("hasspawnegg", definition.hasSpawnEgg())
                    .putString("id", definition.getId())
                    .putInt("rid",  rId++)
                    .putBoolean("summonable", definition.isSummonable())
                    .build());
        }
        this.availableEntities = NbtMap.builder()
                .putList("idlist", NbtType.COMPOUND, entities)
                .build();
    }

    @Override
    protected void loadItemComponents() {
        this.itemComponents.clear();
        for (ItemType itemType : ItemRegistry.getCustomTypes()) {
            this.itemComponents.add(new ComponentItemData(itemType.getItemId(), this.getItemComponentNBT(itemType)));
        }
    }

    protected NbtMap getItemComponentNBT(ItemType itemType) {
        NbtMapBuilder container = NbtMap.builder();
        container.putInt("id", this.getItemRuntimeId(itemType.getItemId()))
                .putString("name", itemType.getItemId());

        NbtMapBuilder components = NbtMap.builder();
        components.putCompound("minecraft:icon", NbtMap.builder()
                .putString("texture", itemType.getIconName())
                .build());

        // Write non-required components if present
        if (itemType instanceof ArmorItemComponent) {
            ArmorItemComponent armorItemComponent = (ArmorItemComponent) itemType;
            components.putCompound("minecraft:armor", NbtMap.builder()
                    .putInt("protection", armorItemComponent.getProtection())
                    .build());
        }
        if (itemType instanceof CooldownItemComponent) {
            CooldownItemComponent cooldownItemComponent = (CooldownItemComponent) itemType;
            components.putCompound("minecraft:cooldown", NbtMap.builder()
                    .putString("category", cooldownItemComponent.getCooldownCategory())
                    .putFloat("duration", (cooldownItemComponent.getCooldownTicks() * 20) / 20f)
                    .build());
        }
        if (itemType instanceof DurableItemComponent) {
            DurableItemComponent durableItemComponent = (DurableItemComponent) itemType;
            components.putCompound("minecraft:durability", NbtMap.builder()
                    .putInt("max_durability", durableItemComponent.getMaxDurability())
                    .build());
        }
        if (itemType instanceof FoodItemComponent) {
            FoodItemComponent foodItemComponent = (FoodItemComponent) itemType;
            components.putCompound("minecraft:food", NbtMap.builder()
                    .putBoolean("can_always_eat", foodItemComponent.canAlwaysBeEaten())
                    .build());
        }
        if (itemType instanceof PlantableItemComponent) {
            components.putCompound("minecraft:block_placer", NbtMap.EMPTY);
        }

        NbtMap itemProperties = NbtMap.builder()
                .putBoolean("allow_off_hand", itemType.isAllowedInOffHand())
                .putInt("creative_category", 2)
                .putInt("damage", itemType.getDamage())
                .putBoolean("foil", itemType.hasFoil())
                .putBoolean("hand_equipped", itemType.isHandEquipped())
                .putBoolean("liquid_clipped", itemType.canClickOnLiquids())
                .putInt("max_stack_size", itemType.getMaxStackSize())
                .putFloat("mining_speed", 0)  // Block breaking is handled server-side. Doing this gives greater block break control in the item type class
                .putBoolean("mirrored_art", itemType.isMirroredArt())
                .putBoolean("stacked_by_data", itemType.isStackedByDamage())
                .putInt("use_animation", itemType.getUseAnimationType().ordinal())
                .putInt("use_duration", itemType.getUseDuration())
                .build();
        components.putCompound("item_properties", itemProperties);

        container.putCompound("components", components.build());
        return container.build();
    }

}
