/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPack;

/**
 *
 * @author Dasty
 */
public class TimeForDistanceTwoSpeed
{
    private double gearRatio1;
    private double gearRatio2;
    private double numMotorsPerGearbox = 3;
    private double gearBoxesPerDriveTrain = 2;
    private double wheelDiameter = 4; //inches
    private double robotWeight = 150; //pounds
    private double motorStallTorque = 1.6374; //N-m
    private double motorFreeSpeed = 5280; //rpm
    private double driveTrainEff = 0.95;
    private double speedLossConstant = 0.75;
    private double timeToShift = .5; //seconds
    
    public TimeForDistanceTwoSpeed(double distance) {
        double shortestTimeToDistance = Integer.MAX_VALUE;
        double topSpeed1 = 0;
        double topSpeed2 = 0;
        for(double Ratio2 = 1; Ratio2 < 25; Ratio2 += 0.05) {
            for(double Ratio1 = Ratio2; Ratio1 < 25; Ratio1 += 0.05) {
                double wheelStallTorque1 = (motorStallTorque * numMotorsPerGearbox) / (1/Ratio1);
                double appliedTorque1 = wheelStallTorque1 * gearBoxesPerDriveTrain;
                double acceleration1 = (appliedTorque1)/((wheelDiameter / 24) * robotWeight) * driveTrainEff;
                double maxWheelRPM1 = motorFreeSpeed * (1/Ratio1) * speedLossConstant;
                
                double maxSpeed1OverDistance = Math.sqrt(2 * acceleration1 * distance);
                double maxSpeed1 = Math.min(maxWheelRPM1 * (wheelDiameter / 12 * Math.PI) / 60, maxSpeed1OverDistance);
                double timeToMaxSpeed1 = maxSpeed1 / acceleration1;
                double distanceToMaxSpeed1 = Math.min(.5 * Math.pow(maxSpeed1,2) / acceleration1, distance);
                
                double distanceToShift = Math.min(distance-distanceToMaxSpeed1, maxSpeed1 * timeToShift);
                
                double wheelStallTorque2 = (motorStallTorque * numMotorsPerGearbox) / (1/Ratio2);
                double appliedTorque2 = wheelStallTorque2 * gearBoxesPerDriveTrain;
                double acceleration2 = (appliedTorque2)/((wheelDiameter / 24) * robotWeight) * driveTrainEff;
                double maxWheelRPM2 = motorFreeSpeed * (1/Ratio2) * speedLossConstant;
                
                double maxSpeed2OverDistance = Math.sqrt(Math.pow(maxSpeed1,2) + 2 * acceleration2 * distance-distanceToMaxSpeed1-distanceToShift);
                double maxSpeed2 = Math.min(maxWheelRPM2 * (wheelDiameter / 12 * Math.PI) / 60, maxSpeed2OverDistance);
                double timeToMaxSpeed2 = (maxSpeed2-maxSpeed1) / acceleration2;
                double distanceToMaxSpeed2 = maxSpeed1 * timeToMaxSpeed2 + .5 * acceleration2 * Math.pow(timeToMaxSpeed2, 2);

                double timeToDistance = timeToMaxSpeed1 + (distanceToShift/maxSpeed1) + timeToMaxSpeed2 + (distance-(distanceToMaxSpeed1+distanceToShift+distanceToMaxSpeed2))/maxSpeed2;
                
                if(timeToDistance < shortestTimeToDistance) {
                    shortestTimeToDistance = timeToDistance;
                    gearRatio1 = Ratio1;
                    gearRatio2 = Ratio2;
                    topSpeed1 = maxSpeed1;
                    topSpeed2 = maxSpeed2;
                }
            }
        }
        
        System.out.println("For Distance: "+distance+" feet - Optimal Ratios: 1st: "+gearRatio1+" => Second: "+gearRatio2+" - Speeds: 1st: "+topSpeed1+" => 2nd: "+topSpeed2+" - Time to Distance: "+shortestTimeToDistance+" - Ratio Spread: "+(gearRatio1/gearRatio2));
    }
    
    public static void main(String[] args) {
        new TimeForDistanceTwoSpeed(27);
    }
}
