package com.majruszsdifficulty.events.bloodmoon.listeners;

import com.mlib.contexts.OnClampedRegionalDifficultyGet;

public class DifficultyIncreaser {
	static {
		OnClampedRegionalDifficultyGet.listen( DifficultyIncreaser::increase );
	}

	private static void increase( OnClampedRegionalDifficultyGet data ) {
		data.crd += 0.5f;
	}
}
