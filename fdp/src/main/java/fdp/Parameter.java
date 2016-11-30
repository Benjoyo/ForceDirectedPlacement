package fdp;

public class Parameter {

	private int frameWidth;
	private int frameHeight;
	private boolean equilibriumCriterion;
	private String attractiveForceString;
	private String repulsiveForceString;
	private double criterionValue;
	private double coolingRateValue;
	private int frameDelayValue;

	public int getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	public boolean isEquilibriumCriterion() {
		return equilibriumCriterion;
	}

	public void setEquilibriumCriterion(boolean equilibriumCriterion) {
		this.equilibriumCriterion = equilibriumCriterion;
	}

	public String getAttractiveForce() {
		return attractiveForceString;
	}

	public void setAttractiveForce(String attractiveForceString) {
		this.attractiveForceString = attractiveForceString;
	}

	public String getRepulsiveForce() {
		return repulsiveForceString;
	}

	public void setRepulsiveForce(String repulsiveForceString) {
		this.repulsiveForceString = repulsiveForceString;
	}

	public double getCriterion() {
		return criterionValue;
	}

	public void setCriterion(double criterionValue) {
		this.criterionValue = criterionValue;
	}

	public double getCoolingRate() {
		return coolingRateValue;
	}

	public void setCoolingRate(double coolingRateValue) {
		this.coolingRateValue = coolingRateValue;
	}

	public int getFrameDelay() {
		return frameDelayValue;
	}

	public void setFrameDelay(int frameDelayValue) {
		this.frameDelayValue = frameDelayValue;
	}

}
