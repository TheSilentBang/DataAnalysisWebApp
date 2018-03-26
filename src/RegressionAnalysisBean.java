package uic.edu.ids517.s17g310;

public class RegressionAnalysisBean {

	private String message;
	private String regressionEq;
	private double intercept;
	private double interceptStdErr;
	private double tStat;
	private double interceptPVal;
	private double slope;
	private double predDF;
	private double residualErrDF;
	private double totalDF;
	private double regressionSumSq;
	private double sumSqErr;
	private double totalSumSq;
	private double meanSq;
	private double meanSqErr;
	private double fVal;
	private double pVal;
	private double slopeStdErr;
	private double tStatPred;
	private double pValPred;
	private double stdErrModel;
	private double rSq;
	private double adjustedRsq;
	
	public RegressionAnalysisBean()
	{}
	
	public boolean setRegressionAnalysisVariables(
			String regressionEq, double intercept,
			double interceptStdErr, double tStat,
			double interceptPVal, double slope,
			double slopeStdErr, double tStatPred,
			double pValPred, double stdErrModel,
			double rSq, double adjustedRsq,
			double predDF,double residualErrDF,
			double totalDF, double regressionSumSq,
			double sumSqErr, double totalSumSq,
			double meanSq, double meanSqErr,
			double fVal, double pVal)
	{
		try {
			this.intercept = intercept;
			this.interceptStdErr = interceptStdErr;
			this.tStat = tStat;
			this.interceptPVal = interceptPVal;
			this.slope = slope;
			this.slopeStdErr = slopeStdErr;
			this.tStatPred = tStatPred;
			this.pValPred = pValPred;
			this.stdErrModel = stdErrModel;
			this.rSq = rSq;
			this.adjustedRsq = adjustedRsq;
			this.predDF = predDF;
			this.residualErrDF = residualErrDF;
			this.totalDF = totalDF;
			this.regressionSumSq = regressionSumSq;
			this.sumSqErr = sumSqErr;
			this.totalSumSq = totalSumSq;
			this.meanSq = meanSq;
			this.meanSqErr = meanSqErr;
			this.fVal = fVal;
			this.pVal = pVal;
			this.regressionEq = regressionEq;
			return true;
		} catch(Exception e) {
			message = e.getMessage();
			return false;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRegressionEq() {
		return regressionEq;
	}

	public void setRegressionEq(String regressionEq) {
		this.regressionEq = regressionEq;
	}

	public double getIntercept() {
		return intercept;
	}

	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}

	public double getInterceptStdErr() {
		return interceptStdErr;
	}

	public void setInterceptStdErr(double interceptStdErr) {
		this.interceptStdErr = interceptStdErr;
	}

	public double gettStat() {
		return tStat;
	}

	public void settStat(double tStat) {
		this.tStat = tStat;
	}

	public double getInterceptPVal() {
		return interceptPVal;
	}

	public void setInterceptPVal(double interceptPVal) {
		this.interceptPVal = interceptPVal;
	}

	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public double getPredDF() {
		return predDF;
	}

	public void setPredDF(double predDF) {
		this.predDF = predDF;
	}

	public double getResidualErrDF() {
		return residualErrDF;
	}

	public void setResidualErrDF(double residualErrDF) {
		this.residualErrDF = residualErrDF;
	}

	public double getTotalDF() {
		return totalDF;
	}

	public void setTotalDF(double totalDF) {
		this.totalDF = totalDF;
	}

	public double getRegressionSumSq() {
		return regressionSumSq;
	}

	public void setRegressionSumSq(double regressionSumSq) {
		this.regressionSumSq = regressionSumSq;
	}

	public double getSumSqErr() {
		return sumSqErr;
	}

	public void setSumSqErr(double sumSqErr) {
		this.sumSqErr = sumSqErr;
	}

	public double getTotalSumSq() {
		return totalSumSq;
	}

	public void setTotalSumSq(double totalSumSq) {
		this.totalSumSq = totalSumSq;
	}

	public double getMeanSq() {
		return meanSq;
	}

	public void setMeanSq(double meanSq) {
		this.meanSq = meanSq;
	}

	public double getMeanSqErr() {
		return meanSqErr;
	}

	public void setMeanSqErr(double meanSqError) {
		this.meanSqErr = meanSqError;
	}

	public double getfVal() {
		return fVal;
	}

	public void setfVal(double fVal) {
		this.fVal = fVal;
	}

	public double getpVal() {
		return pVal;
	}

	public void setpVal(double pVal) {
		this.pVal = pVal;
	}

	public double getSlopeStdErr() {
		return slopeStdErr;
	}

	public void setSlopeStdErr(double slopeStdErr) {
		this.slopeStdErr = slopeStdErr;
	}

	public double gettStatPred() {
		return tStatPred;
	}

	public void settStatPred(double tStatPred) {
		this.tStatPred = tStatPred;
	}

	public double getpValPred() {
		return pValPred;
	}

	public void setpValPred(double pValPred) {
		this.pValPred = pValPred;
	}

	public double getStdErrModel() {
		return stdErrModel;
	}

	public void setStdErrModel(double stdErrModel) {
		this.stdErrModel = stdErrModel;
	}

	public double getrSq() {
		return rSq;
	}

	public void setrSq(double rSq) {
		this.rSq = rSq;
	}

	public double getAdjustedRsq() {
		return adjustedRsq;
	}

	public void setAdjustedRsq(double adjustedRsq) {
		this.adjustedRsq = adjustedRsq;
	}

}