/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.milkenknights;


import com.milkenknights.InsightLT.DecimalData;
import com.milkenknights.InsightLT.InsightLT;
import com.milkenknights.InsightLT.StringData;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Talon;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Knight extends IterativeRobot {
	private static final int leftMotor = 8;
	private static final int rightMotor = 5;
	
	private static final double jitterRange = 0.008;
	
	JStick xbox;
	private boolean usingCheesy;
	private DriverStationLCD lcd;
    
    private Compressor compressor;
	private DoubleSolenoid solenoids;

	private Preferences prefs;
	
	private Drive drive;

	private InsightLT display;
	private DecimalData disp_batteryVoltage;
	private StringData disp_message;
	
	public Knight() {
		prefs = Preferences.getInstance();
		drive = new Drive(new Talon(prefs.getInt("leftmotor",leftMotor)),
							new Talon(prefs.getInt("rightmotor",rightMotor)));
		xbox = new JStick(1);
		lcd = DriverStationLCD.getInstance();

		usingCheesy = false;
		integral_err = 0;
		prev_err = 0;

        compressor = new Compressor(1,1);
		solenoids = new DoubleSolenoid(1,2);

		display = new InsightLT(InsightLT.TWO_ONE_LINE_ZONES);
		display.startDisplay();

		disp_batteryVoltage = new DecimalData("Bat:");
		display.registerData(disp_batteryVoltage,1);

		disp_message = new StringData();
		display.registerData(disp_message,2);
	}
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        compressor.start();
		solenoids.set(DoubleSolenoid.Value.kForward);
    }

    /**
     * This function is called periodically during autonomous
     */
    double integral_err;
    double prev_err;
    public void autonomousPeriodic() {
    	drive.tankDrive(0.4, 0.4);
		disp_batteryVoltage.setData(DriverStation.getInstance().getBatteryVoltage());
		disp_message.setData("autonomous");
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
		xbox.update();
		
		// Press A to toggle cheesy drive
		if (xbox.isReleased(JStick.XBOX_A)) {
			usingCheesy = !usingCheesy;
		}

		// use RB and LB to control the solenoids
		if (xbox.isReleased(JStick.XBOX_RB)) {
			solenoids.set(DoubleSolenoid.Value.kForward);
		}
		if (xbox.isReleased(JStick.XBOX_LB)) {
			solenoids.set(DoubleSolenoid.Value.kReverse);
		}

		// show the solenoids status
		switch (solenoids.get().value) {
		case DoubleSolenoid.Value.kForward_val:
			lcd.println(DriverStationLCD.Line.kUser3,1,"High Gear");
			break;
			
		case DoubleSolenoid.Value.kReverse_val:
			lcd.println(DriverStationLCD.Line.kUser3,1,"Low Gear");
			break;
		}

		// show if the compressor is running
		if (compressor.getPressureSwitchValue()) {
			lcd.println(DriverStationLCD.Line.kUser6,1,"Compressor is running");
		} else {
			lcd.println(DriverStationLCD.Line.kUser6,1,"Compressor is off");
		}
		
		double leftStickX = JStick.removeJitter(xbox.getAxis(JStick.XBOX_LSX), jitterRange);
		double leftStickY = JStick.removeJitter(xbox.getAxis(JStick.XBOX_LSY), jitterRange);
		double rightStickY = JStick.removeJitter(xbox.getAxis(JStick.XBOX_RSY), jitterRange);

		if (usingCheesy) {
			drive.cheesyDrive(rightStickY, leftStickX, xbox.isPressed(JStick.XBOX_LJ));
			lcd.println(DriverStationLCD.Line.kUser4,1,"cheesy");
		} else {
			if (!drive.straightDrive(xbox.getAxis(JStick.XBOX_TRIG))) {
				drive.tankDrive(leftStickY, rightStickY);
				lcd.println(DriverStationLCD.Line.kUser4,1,"tank drive");
			} else {
				lcd.println(DriverStationLCD.Line.kUser4,1,"straightDrive");
			}
		}
		lcd.updateLCD();

		// update the display
		disp_batteryVoltage.setData(DriverStation.getInstance().getBatteryVoltage());
		disp_message.setData("teleop");
    }

	public void disabledPeriodic() {
		disp_batteryVoltage.setData(DriverStation.getInstance().getBatteryVoltage());
		disp_message.setData("disabled");
	}
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
