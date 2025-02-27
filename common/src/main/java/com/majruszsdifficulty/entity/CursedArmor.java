package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.Animations;
import com.majruszlibrary.animations.AnimationsDef;
import com.majruszlibrary.animations.IAnimableEntity;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityPreDamaged;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.OnEntityTicked;
import com.majruszlibrary.events.OnLootGenerated;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.item.LootHelper;
import com.majruszlibrary.level.BlockHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszlibrary.modhelper.Resource;
import com.majruszlibrary.text.TextHelper;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CursedArmor extends Monster implements IAnimableEntity {
	private static final Resource< AnimationsDef > ANIMATIONS = MajruszsDifficulty.HELPER.load( "cursed_armor_animation", AnimationsDef.class );
	private static float ITEM_DROP_CHANCE = 0.2f;
	private static float NAME_CHANCE = 0.025f;
	private static List< String > NAMES = List.of( "Freshah" );
	private static List< LocationDef > LOCATIONS = List.of(
		new LocationDef(
			MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_dungeon" ),
			List.of( new ResourceLocation( "chests/simple_dungeon" ) ),
			0.5f
		),
		new LocationDef(
			MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_stronghold" ),
			List.of(
				new ResourceLocation( "chests/stronghold_corridor" ),
				new ResourceLocation( "chests/stronghold_crossing" ),
				new ResourceLocation( "chests/stronghold_library" )
			),
			0.4f
		),
		new LocationDef(
			MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_portal" ),
			List.of( new ResourceLocation( "chests/ruined_portal" ) ),
			1.0f
		),
		new LocationDef(
			MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_nether" ),
			List.of(
				new ResourceLocation( "chests/bastion_bridge" ),
				new ResourceLocation( "chests/bastion_hoglin_stable" ),
				new ResourceLocation( "chests/bastion_other" ),
				new ResourceLocation( "chests/bastion_treasure" ),
				new ResourceLocation( "chests/nether_bridge" )
			),
			0.25f
		),
		new LocationDef(
			MajruszsDifficulty.HELPER.getLocation( "gameplay/cursed_armor_end" ),
			List.of( new ResourceLocation( "chests/end_city_treasure" ) ),
			0.5f
		)
	);
	private final Animations animations = Animations.create();

	static {
		OnLootGenerated.listen( CursedArmor::spawnCursedArmor )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.getLevel() != null )
			.addCondition( data->data.origin != null )
			.addCondition( data->BlockHelper.getEntity( data.getLevel(), data.origin ) instanceof ChestBlockEntity )
			.addCondition( data->Random.check( CursedArmor.find( data.lootId ).map( def->def.chance ).orElse( 0.0f ) ) );

		OnEntitySpawned.listen( CursedArmor::giveRandomArmor )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity instanceof CursedArmor );

		OnEntitySpawned.listen( CursedArmor::setCustomName )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chance( ()->CursedArmor.NAME_CHANCE ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity instanceof CursedArmor );

		OnEntityTicked.listen( CursedArmor::spawnIdleParticles )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 0.2f ) )
			.addCondition( data->data.entity instanceof CursedArmor );

		OnEntityTicked.listen( CursedArmor::spawnAssemblingParticles )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 0.2f ) )
			.addCondition( data->data.entity instanceof CursedArmor cursedArmor && cursedArmor.isAssembling() );

		OnEntityPreDamaged.listen( OnEntityPreDamaged::cancelDamage )
			.addCondition( data->data.target instanceof CursedArmor cursedArmor && cursedArmor.isAssembling() );

		Serializables.getStatic( com.majruszsdifficulty.data.Config.Mobs.class )
			.define( "cursed_armor", CursedArmor.class );

		Serializables.getStatic( CursedArmor.class )
			.define( "item_drop_chance", Reader.number(), ()->ITEM_DROP_CHANCE, v->ITEM_DROP_CHANCE = Range.CHANCE.clamp( v ) )
			.define( "custom_name_chance", Reader.number(), ()->NAME_CHANCE, v->NAME_CHANCE = Range.CHANCE.clamp( v ) )
			.define( "custom_names", Reader.list( Reader.string() ), ()->NAMES, v->NAMES = v )
			.define( "locations", Reader.list( Reader.custom( LocationDef::new ) ), ()->LOCATIONS, v->LOCATIONS = v );

		Serializables.get( LocationDef.class )
			.define( "loot", Reader.location(), s->s.loot, ( s, v )->s.loot = v )
			.define( "chests", Reader.list( Reader.location() ), s->s.chests, ( s, v )->s.chests = v )
			.define( "chance", Reader.number(), s->s.chance, ( s, v )->s.chance = v );
	}

	public static EntityType< CursedArmor > createEntityType() {
		return EntityType.Builder.of( CursedArmor::new, MobCategory.MONSTER )
			.sized( 0.5f, 1.9f )
			.build( "cursed_armor" );
	}

	public static AttributeSupplier createAttributes() {
		return Monster.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 30.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.23 )
			.add( Attributes.ATTACK_DAMAGE, 3.0 )
			.add( Attributes.FOLLOW_RANGE, 35.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.23 )
			.add( Attributes.ARMOR, 4.0 )
			.build();
	}

	public static boolean checkMonsterSpawnRules( EntityType< ? extends Monster > entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType,
		BlockPos blockPos, RandomSource random
	) {
		return Monster.checkMonsterSpawnRules( entityType, level, mobSpawnType, blockPos, random ) && BloodMoonHelper.isActive();
	}

	public CursedArmor( EntityType< ? extends Monster > entityType, Level level ) {
		super( entityType, level );
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public int getExperienceReward() {
		return 7;
	}

	@Override
	public AnimationsDef getAnimationsDef() {
		return ANIMATIONS.get();
	}

	@Override
	public Animations getAnimations() {
		return this.animations;
	}

	public void assemble() {
		if( this.animations.isEmpty() ) {
			this.playAnimation( "assemble" )
				.addCallback( 27, ()->{
					if( this.getItemBySlot( EquipmentSlot.CHEST ).getItem() instanceof ArmorItem armorItem ) {
						SoundEmitter.of( armorItem.getEquipSound() )
							.source( SoundSource.HOSTILE )
							.position( this.position() )
							.emit( this.level() );
					}
				} );
		}
	}

	public void equip( LocationDef locationDef ) {
		LootHelper.getLootTable( locationDef.loot )
			.getRandomItems( LootHelper.toGiftParams( this ) )
			.forEach( itemStack->{
				if( itemStack.getItem() instanceof ShieldItem ) {
					this.setItemSlot( EquipmentSlot.OFFHAND, itemStack );
				} else {
					ItemHelper.equip( this, itemStack );
				}
			} );

		EquipmentSlots.ALL.forEach( slot->this.setDropChance( slot, ITEM_DROP_CHANCE ) );
	}

	public boolean isAssembling() {
		return !this.animations.isEmpty();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new AssembleGoal( this ) );
		this.goalSelector.addGoal( 2, new MeleeAttackGoal( this, 1.0, false ) );
		this.goalSelector.addGoal( 3, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 4, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 4, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
	}

	private static Optional< LocationDef > find( ResourceLocation chestId ) {
		return LOCATIONS.stream()
			.filter( locationDef->locationDef.chests.stream().anyMatch( chestId::equals ) )
			.findFirst();
	}

	private static LocationDef getRandomLocationDef() {
		return Random.next( LOCATIONS );
	}

	private static void spawnCursedArmor( OnLootGenerated data ) {
		TimeHelper.nextTick( delay->{
			CursedArmor cursedArmor = EntityHelper.createSpawner( MajruszsDifficulty.CURSED_ARMOR_ENTITY, data.getLevel() )
				.position( CursedArmor.getSpawnPosition( data ) )
				.beforeEvent( entity->{
					float yRot = BlockHelper.getState( data.getLevel(), data.origin ).getValue( ChestBlock.FACING ).toYRot();
					entity.setYRot( yRot );
					entity.setYHeadRot( yRot );
					entity.setYBodyRot( yRot );
				} )
				.spawn();
			if( cursedArmor != null ) {
				cursedArmor.assemble();
				cursedArmor.equip( CursedArmor.find( data.lootId ).orElseThrow() );
				if( data.entity instanceof ServerPlayer player ) {
					TimeHelper.nextTick( subdelay->player.closeContainer() );
				}
			}
		} );
	}

	private static void giveRandomArmor( OnEntitySpawned data ) {
		CursedArmor cursedArmor = ( CursedArmor )data.entity;
		if( cursedArmor.getArmorCoverPercentage() == 0.0f ) {
			cursedArmor.assemble();
			cursedArmor.equip( CursedArmor.getRandomLocationDef() );
		}
	}

	private static void setCustomName( OnEntitySpawned data ) {
		data.entity.setCustomName( TextHelper.literal( Random.next( CursedArmor.NAMES ) ) );
	}

	private static Vec3 getSpawnPosition( OnLootGenerated data ) {
		ServerLevel level = data.getServerLevel();
		Function< Float, Boolean > isAir = y->BlockHelper.getState( level, data.origin.add( 0.0, y, 0.0 ) ).isAir();
		if( isAir.apply( 1.0f ) && isAir.apply( 2.0f ) ) {
			return data.origin.add( 0.0, 0.5, 0.0 );
		} else {
			Vec3i offset = BlockHelper.getState( level, data.origin ).getValue( ChestBlock.FACING ).getNormal();
			return data.origin.add( offset.getX(), offset.getY(), offset.getZ() );
		}
	}

	private static void spawnIdleParticles( OnEntityTicked data ) {
		CursedArmor.spawnParticles( data, new Vec3( 0.0, data.entity.getBbHeight() * 0.5, 0.0 ), 0.3, 1 );
	}

	private static void spawnAssemblingParticles( OnEntityTicked data ) {
		CursedArmor.spawnParticles( data, new Vec3( 0.0, 0.0, 0.0 ), 0.6, 5 );
	}

	private static void spawnParticles( OnEntityTicked data, Vec3 emitterOffset, double offsetMultiplier, int particlesCount ) {
		ParticleEmitter.of( ParticleTypes.ENCHANT )
			.position( data.entity.position().add( emitterOffset ) )
			.offset( ()->AnyPos.from( data.entity.getBbWidth(), data.entity.getBbHeight(), data.entity.getBbWidth() ).mul( offsetMultiplier ).vec3() )
			.speed( 0.5f )
			.count( particlesCount )
			.emit( data.getLevel() );
	}

	public static class AssembleGoal extends Goal {
		private final CursedArmor cursedArmor;

		public AssembleGoal( CursedArmor cursedArmor ) {
			this.cursedArmor = cursedArmor;

			this.setFlags( EnumSet.of( Flag.MOVE, Flag.LOOK ) );
		}

		@Override
		public boolean canUse() {
			return this.cursedArmor.isAssembling();
		}
	}

	public static class LocationDef {
		public ResourceLocation loot;
		public List< ResourceLocation > chests;
		public float chance;

		public LocationDef( ResourceLocation loot, List< ResourceLocation > chests, float chance ) {
			this.loot = loot;
			this.chests = chests;
			this.chance = chance;
		}

		public LocationDef() {
			this( null, List.of(), 0.0f );
		}
	}
}
