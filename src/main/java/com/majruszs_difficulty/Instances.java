package com.majruszs_difficulty;

import com.majruszs_difficulty.blocks.EndBlock;
import com.majruszs_difficulty.blocks.EndShardOre;
import com.majruszs_difficulty.effects.BleedingEffect;
import com.majruszs_difficulty.entities.EntitiesConfig;
import com.majruszs_difficulty.events.ExperienceBonus;
import com.majruszs_difficulty.events.FallDamageWithNegativeEffects;
import com.majruszs_difficulty.events.FishingRewarder;
import com.majruszs_difficulty.events.monster_spawn.*;
import com.majruszs_difficulty.events.undead_army.UndeadArmyConfig;
import com.majruszs_difficulty.events.when_damaged.*;
import com.majruszs_difficulty.items.*;
import com.majruszs_difficulty.structure_pieces.FlyingEndIslandPiece;
import com.majruszs_difficulty.structure_pieces.FlyingPhantomPiece;
import com.majruszs_difficulty.structures.FlyingEndIslandStructure;
import com.majruszs_difficulty.structures.FlyingPhantomStructure;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.ModLoadingContext;

public class Instances {
	public static final ItemGroup ITEM_GROUP = new CustomItemGroup( "majruszs_tab" );

	// Items
	public static final UndeadBattleStandardItem BATTLE_STANDARD_ITEM;
	public static final BandageItem BANDAGE_ITEM;
	public static final FishermanEmblemItem FISHERMAN_EMBLEM_ITEM;
	public static final HermesBootsItem HERMES_BOOTS_ITEM;
	public static final EndShardItem END_SHARD_ITEM;
	public static final EndIngotItem END_INGOT_ITEM;
	public static final EndSwordItem END_SWORD_ITEM;
	public static final EndShovelItem END_SHOVEL_ITEM;
	public static final EndPickaxeItem END_PICKAXE_ITEM;
	public static final EndAxeItem END_AXE_ITEM;
	public static final EndHoeItem END_HOE_ITEM;
	public static final TatteredClothItem TATTERED_CLOTH_ITEM;

	// Treasure Bags
	public static final TreasureBagItem UNDEAD_ARMY_TREASURE_BAG;
	public static final TreasureBagItem ELDER_GUARDIAN_TREASURE_BAG;
	public static final TreasureBagItem WITHER_TREASURE_BAG;
	public static final TreasureBagItem ENDER_DRAGON_TREASURE_BAG;
	public static final TreasureBagItem FISHING_TREASURE_BAG;

	// Blocks
	public static final EndShardOre END_SHARD_ORE;
	public static final EndShardOre.EndShardOreItem END_SHARD_ORE_ITEM;
	public static final EndBlock END_BLOCK;
	public static final EndBlock.EndBlockItem END_BLOCK_ITEM;

	// Entities
	public static final EntitiesConfig ENTITIES_CONFIG;

	// Effects
	public static final BleedingEffect BLEEDING;

	// Particles
	public static final BasicParticleType BLOOD_PARTICLE;

	// Structures
	public static final FlyingPhantomStructure FLYING_PHANTOM;
	public static final FlyingEndIslandStructure FLYING_END_ISLAND;
	public static final StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > FLYING_PHANTOM_FEATURE;
	public static final StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > FLYING_END_ISLAND_FEATURE;
	public static final IStructurePieceType FLYING_PHANTOM_PIECE;
	public static final IStructurePieceType FLYING_END_ISLAND_PIECE;

	// Misc
	public static final UndeadArmyConfig UNDEAD_ARMY_CONFIG;
	public static final ExperienceBonus EXPERIENCE_BONUS;
	public static final FallDamageWithNegativeEffects FALL_DAMAGE_EFFECTS;
	public static final FishingRewarder FISHING_REWARDER;

	static {
		// Items
		BATTLE_STANDARD_ITEM = new UndeadBattleStandardItem();
		BANDAGE_ITEM = new BandageItem();
		FISHERMAN_EMBLEM_ITEM = new FishermanEmblemItem();
		HERMES_BOOTS_ITEM = new HermesBootsItem();
		END_SHARD_ITEM = new EndShardItem();
		END_INGOT_ITEM = new EndIngotItem();
		END_SWORD_ITEM = new EndSwordItem();
		END_SHOVEL_ITEM = new EndShovelItem();
		END_PICKAXE_ITEM = new EndPickaxeItem();
		END_AXE_ITEM = new EndAxeItem();
		END_HOE_ITEM = new EndHoeItem();
		TATTERED_CLOTH_ITEM = new TatteredClothItem();

		// Treasure Bags
		UNDEAD_ARMY_TREASURE_BAG = new TreasureBagItem( "undead_army", "Undead Army" );
		ELDER_GUARDIAN_TREASURE_BAG = new TreasureBagItem( "elder_guardian", "Elder Guardian" );
		WITHER_TREASURE_BAG = new TreasureBagItem( "wither", "Wither" );
		ENDER_DRAGON_TREASURE_BAG = new TreasureBagItem( "ender_dragon", "Ender Dragon" );
		FISHING_TREASURE_BAG = new TreasureBagItem( "fishing", "Fishing" );

		// Blocks
		END_SHARD_ORE = new EndShardOre();
		END_SHARD_ORE_ITEM = new EndShardOre.EndShardOreItem();
		END_BLOCK = new EndBlock();
		END_BLOCK_ITEM = new EndBlock.EndBlockItem();

		// Entities
		ENTITIES_CONFIG = new EntitiesConfig();

		// Effects
		BLEEDING = new BleedingEffect();

		// Particles
		BLOOD_PARTICLE = new BasicParticleType( true );

		// Structures
		ResourceLocation flyingPhantomResource = MajruszsDifficulty.getLocation( "flying_phantom_structure" );
		FLYING_PHANTOM = new FlyingPhantomStructure();
		FLYING_PHANTOM_FEATURE = WorldGenRegistries.register( WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, flyingPhantomResource.toString(),
			FLYING_PHANTOM.withConfiguration( NoFeatureConfig.field_236559_b_ )
		);
		FLYING_PHANTOM_PIECE = IStructurePieceType.register( FlyingPhantomPiece::new, flyingPhantomResource.toString() );
		ResourceLocation flyingEndIslandResource = MajruszsDifficulty.getLocation( "flying_end_island_structure" );
		FLYING_END_ISLAND = new FlyingEndIslandStructure();
		FLYING_END_ISLAND_FEATURE = WorldGenRegistries.register( WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, flyingEndIslandResource.toString(),
			FLYING_END_ISLAND.withConfiguration( NoFeatureConfig.field_236559_b_ )
		);
		FLYING_END_ISLAND_PIECE = IStructurePieceType.register( FlyingEndIslandPiece::new, flyingEndIslandResource.toString() );

		// When damaged events
		WhenDamagedEvent.REGISTRY_LIST.add( new SpiderPoisonOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new SkyKeeperLevitationOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new DrownedLightningOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new NauseaAndWeaknessWhenDrowning() );
		WhenDamagedEvent.REGISTRY_LIST.add( new WitherSwordOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new CactusBleedingOnHurt() );
		WhenDamagedEvent.REGISTRY_LIST.add( new SharpItemBleedingOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new ArrowBleedingOnHurt() );
		WhenDamagedEvent.REGISTRY_LIST.add( new ThrownTridentBleedingOnHurt() );
		WhenDamagedEvent.REGISTRY_LIST.add( new BiteBleedingOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new EndermanTeleportOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new EndSwordLevitationOnAttack() );
		WhenDamagedEvent.REGISTRY_LIST.add( new TriggerAllEndermansOnAttack() );

		// On enemy to be spawned
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new StrengthenedEntityAttributesOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new GiveWitherSkeletonSwordOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new GiveEvokerTotemOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new ChargeCreeperOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new ApplyingNegativeEffectOnCreeperOnSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnPiglinGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnPillagerGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnSkeletonGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnZombieGroup() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new CreateJockeyOnSpiderSpawn() );
		OnEnemyToBeSpawnedEvent.REGISTRY_LIST.add( new SpawnEliteSkeletonGroup() );

		// Misc
		UNDEAD_ARMY_CONFIG = new UndeadArmyConfig();
		EXPERIENCE_BONUS = new ExperienceBonus();
		FALL_DAMAGE_EFFECTS = new FallDamageWithNegativeEffects();
		FISHING_REWARDER = new FishingRewarder();

		MajruszsDifficulty.CONFIG_HANDLER.register( ModLoadingContext.get() );
	}

	public static class Tools {
		public static final SwordItem WITHER_SWORD;

		static {
			WITHER_SWORD = new WitherSwordItem();
		}
	}

	public static class Sounds {
		public static final SoundEvent UNDEAD_ARMY_APPROACHING;
		public static final SoundEvent UNDEAD_ARMY_WAVE_STARTED;

		static {
			UNDEAD_ARMY_APPROACHING = new SoundEvent( MajruszsDifficulty.getLocation( "undead_army.approaching" ) );
			UNDEAD_ARMY_WAVE_STARTED = new SoundEvent( MajruszsDifficulty.getLocation( "undead_army.wave_started" ) );
		}
	}

	public static class DamageSources {
		public static final DamageSource BLEEDING;

		static {
			BLEEDING = new DamageSource( "bleeding" ).setDamageBypassesArmor();
		}
	}

}
