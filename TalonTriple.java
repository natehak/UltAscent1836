package com.milkenknights;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;

public class TalonTriple implements SpeedController {
	private Talon ma;
	private Talon mb;
	private Talon mc;

	private boolean reva;
	private boolean revb;
	private boolean revc;

	private double speed;
	
	public TalonTriple(int a, int b, int c) {
		this(a,false,b,false,c,false);
	}

	public TalonTriple(int a, boolean arev, int b, boolean brev, int c, boolean crev) {
		ma = new Talon(a);
		ma.set(0);
		reva = arev;

		mb = new Talon(b);
		mb.set(0);
		revb = brev;
		
		mc = new Talon(c);
		mc.set(0);
		revc = crev;
	}

	@Override
	public void disable() {
		ma.disable();
		mb.disable();
		mc.disable();
	}

	@Override
	public double get() {
		return speed;
	}

	public double getA() {
		return ma.get();
	}
	public double getB() {
		return mb.get();
	}
	public double getC() {
		return mc.get();
	}

	@Override
	public void set(double newspeed) {
		ma.set(reva ? -newspeed : newspeed);
		mb.set(revb ? -newspeed : newspeed);
		mc.set(revc ? -newspeed : newspeed);
	}

	@Override
	public void set(double newspeed, byte arg1) {
		set(newspeed);
	}
	@Override
	public void pidWrite(double output) {
		ma.pidWrite(reva ? -output : output);
		mb.pidWrite(revb ? -output : output);
		mc.pidWrite(revc ? -output : output);
	}

	
}