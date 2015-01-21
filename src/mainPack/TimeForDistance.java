/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPack;

/**
 *
 * @author Dasty
 */
public class TimeForDistance 
{
    private double gearRatio;
    private double numMotorsPerGearbox = 3;
    private double gearBoxesPerDriveTrain = 2;
    private double wheelDiameter = .1016;
    private double robotWeight = 68.0389;
    private double motorStallTorque = 2.22; 
    private double motorFreeSpeed = 5300; 
    private double driveTrainEff = 0.95;
    private double speedLossConstant = 0.75;
    
    public TimeForDistance(double distance) {
        double shortestTimeToDistance = Integer.MAX_VALUE;
        double topSpeed = 0;
        for(double Ratio = 1; Ratio < 25; Ratio += 0.05) {
            double maxWheelRPM = motorFreeSpeed * (1/Ratio);
            double maxSpeed = maxWheelRPM * wheelDiameter * Math.PI / 60;
            double rpmFinal = 0;
            double v0 = 0;
            double interval = 0.01;
            double timetospeed = 0;
            double disttospeed = 0;
            double speedDist = 0;
            double timetodistance = 10000;
            double rpm0 = 0;
            // Discrete approximation loop.  Using the selected motors torque characteristics
            // loop through the rpm and amperage calculations and accelerate the motor for the
            // interval, accumulating distance traveled and time elapsed until the end RPM equals
            // the no-load rpm.  Account for traction limit and motor stall.
            while (Math.round(rpmFinal) < motorFreeSpeed) {

                    // determine the torque available for acceleration

                    double torque_avail = motorStallTorque*numMotorsPerGearbox*gearBoxesPerDriveTrain/(1/Ratio)/wheelDiameter/2;

                    // Determine the acceleration, A=F/M

                    double accel = torque_avail/robotWeight;

                    // now accelerate using the available torque for the interval
                    
                    double vf = v0+accel*interval;
                    double dist = v0*interval+(accel*interval*interval)/2;
                    rpmFinal = ((vf*60)/(Math.PI*wheelDiameter))*(1/Ratio);

                    // accumulate the results

                    timetospeed+=interval;
                    disttospeed+=dist;

                    if ((disttospeed >= distance)&&(speedDist==0)) {
                            timetodistance = timetospeed;
                            speedDist = vf;
                    }

                    // set up the next iteration
                    // 	cut off the loop once we are no longer accelerating noticeably -or-
                    // 	we are at our max speed or RPM.

                    if ((vf-v0 < .01) || (vf >= maxSpeed)) break;

                    v0=vf;
                    rpm0 = rpmFinal;

            }
            //System.out.println("--"+Ratio);
            if(timetodistance < shortestTimeToDistance) {
                shortestTimeToDistance = timetodistance;
                gearRatio = Ratio;
                topSpeed = disttospeed;
            }
        }
        
        System.out.println("For Distance: "+distance+" feet - Optimal Ratio: "+gearRatio+" - Speed: "+topSpeed+" - Time to Distance: "+shortestTimeToDistance);
    }
    
    public static void main(String[] args) {
        new TimeForDistance(27);
    }
}
