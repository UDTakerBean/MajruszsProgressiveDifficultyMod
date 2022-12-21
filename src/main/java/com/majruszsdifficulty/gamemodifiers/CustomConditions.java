package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.Random;
import com.mlib.config.BooleanConfig;
import com.mlib.config.EnumConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.parameters.Priority;
import com.mlib.math.AABBHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

import static com.majruszsdifficulty.GameStage.Stage;
import static com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig.SIDEKICK_TAG;

public class CustomConditions {
	public static class CRDChance< DataType extends ContextData > extends Condition.Chance< DataType > {
		final BooleanConfig scaledByCRD;

		public CRDChance( double defaultChance, boolean scaledByCRD ) {
			super( defaultChance );

			this.scaledByCRD = new BooleanConfig( "scaled_by_crd", "Specifies whether the chance should be scaled by Clamped Regional Difficulty.", false, scaledByCRD );
			this.addConfig( this.scaledByCRD );
		}

		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			double multiplier = this.scaledByCRD.isEnabled() ? com.majruszsdifficulty.GameStage.getRegionalDifficulty( data.entity ) : 1.0;

			return Random.tryChance( multiplier * this.chance.getOrDefault() );
		}
	}

	public static class GameStage< DataType extends ContextData > extends Condition< DataType > {
		final EnumConfig< Stage > minimumStage;

		public GameStage( Stage minimumStage ) {
			this.minimumStage = new EnumConfig<>( "minimum_stage", "Minimum game stage required for that to happen.", false, minimumStage );
			this.addConfig( this.minimumStage );
			this.apply( params->params.setConfigurable( true ) );
		}

		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			return com.majruszsdifficulty.GameStage.atLeast( this.minimumStage.get() );
		}
	}

	public static class GameStageExact< DataType extends ContextData > extends Condition< DataType > {
		final Stage stage;

		public GameStageExact( Stage stage ) {
			this.stage = stage;
		}

		@Override
		public boolean check( GameModifier gameModifier, ContextData data ) {
			return com.majruszsdifficulty.GameStage.getCurrentStage() == this.stage;
		}
	}

	public static class IsNotSidekick< DataType extends ContextData > extends Condition< DataType > {
		public IsNotSidekick() {
			this.apply( params->params.setPriority( Priority.HIGH ) );
		}

		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			return data.entity instanceof PathfinderMob && !data.entity.getPersistentData().getBoolean( SIDEKICK_TAG );
		}
	}

	public static class IsNotUndeadArmy< DataType extends ContextData > extends Condition< DataType > {
		public IsNotUndeadArmy() {
			this.apply( params->params.setPriority( Priority.HIGH ) );
		}

		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			return !UndeadArmyManager.belongsToUndeadArmy( data.entity );
		}
	}

	public static class IsNotTooManyMobsNearby< DataType extends ContextData > extends Condition< DataType > {
		public IsNotTooManyMobsNearby() {
			this.apply( params->params.setPriority( Priority.LOWEST ) ); // it can significantly affect the performance
		}

		@Override
		public boolean check( GameModifier gameModifier, ContextData data ) {
			if( data.level == null || data.entity == null ) {
				return false;
			}

			return data.level.getEntities( data.entity, AABBHelper.createInflatedAABB( data.entity.position(), 10.0 ), entity->entity instanceof LivingEntity )
				.size() < 15;
		}
	}
}
