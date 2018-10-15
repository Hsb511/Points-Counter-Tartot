package team23.lecompteurdetartot.java_object;

import android.content.Context;
import android.content.res.Resources;

import team23.lecompteurdetartot.R;

public enum Bid {
	PASS(0),
	SMALL(1),
	GUARD(2),
	GUARD_WITHOUT(4),
	GUARD_AGAINST(6);

	int multiplicant ;
	//default -1, for unknown

	private Bid(int i)
	{
		multiplicant = i;
	}

	public int getMultiplicant()
	{
		return multiplicant;
	}

	public String toString(Context context) {
		Resources res = context.getResources();
		switch(this) {
			case PASS: return res.getString(R.string.pass);
			case SMALL: return res.getString(R.string.small);
			case GUARD: return res.getString(R.string.guard);
			case GUARD_WITHOUT: return res.getString(R.string.guard_without);
			case GUARD_AGAINST: return res.getString(R.string.guard_against);
			default: throw new IllegalArgumentException();
		}
	}

	public Bid intToBid(int multiplicant) {
		switch (multiplicant) {
			case 0: return PASS;
			case 1: return SMALL;
			case 2: return GUARD;
			case 4: return GUARD_WITHOUT;
			case 6: return GUARD_AGAINST;
			default: throw new IllegalArgumentException();
		}
	}
}
