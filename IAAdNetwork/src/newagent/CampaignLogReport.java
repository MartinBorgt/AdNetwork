/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.Set;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;

/**
 *
 * @author hsn
 */
public class CampaignLogReport {

    private int campaignId;
    private int day;
    private Long reachImps;
    private int dayStart;
    private int dayEnd;
    private Set<MarketSegment> targetSegment;
    private String targetSegmentName;
    private double videoCoef;
    private double mobileCoef;
    private AdxQuery[] campaignQueries;
    private CampaignStats stats;
    private double budget;
    private String demandAgentAddress;
    private String adxAgentAddress;
    private long budgetMilis;
    private double ucsLevel;
    private double bankStatus;
    private double serviceLevel;
    private double price;
    private double qualityScore;
    private String winner;
    private long secondPrice;
    private long costMillis;
    private double targetedImps;
    private double otherImps;
    private double cost;
    private String winning;

    public CampaignLogReport() {
    }

    public long getSecondPrice() {
        return secondPrice;
    }

    public void setSecondPrice(long secondPrice) {
        this.secondPrice = secondPrice;
    }

    public String getWinning() {
        return winning;
    }

    public void setWinning(String winning) {
        this.winning = winning;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public CampaignStats getStats() {
        return stats;
    }

    public void setStats(CampaignStats stats) {
        this.stats = stats;
    }

    public AdxQuery[] getCampaignQueries() {
        return campaignQueries;
    }

    public void setCampaignQueries(AdxQuery[] campaignQueries) {
        this.campaignQueries = campaignQueries;
    }

    public String getAdxAgentAddress() {
        return adxAgentAddress;
    }

    public void setAdxAgentAddress(String adxAgentAddress) {
        this.adxAgentAddress = adxAgentAddress;
    }

    public double getBankStatus() {
        return bankStatus;
    }

    public void setBankStatus(double bankStatus) {
        this.bankStatus = bankStatus;
    }

    public long getBudgetMilis() {
        return budgetMilis;
    }

    public void setBudgetMilis(long budgetMilis) {
        this.budgetMilis = budgetMilis;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public long getCostMillis() {
        return costMillis;
    }

    public void setCostMillis(long costMillis) {
        this.costMillis = costMillis;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(int dayEnd) {
        this.dayEnd = dayEnd;
    }

    public int getDayStart() {
        return dayStart;
    }

    public void setDayStart(int dayStart) {
        this.dayStart = dayStart;
    }

    public String getDemandAgentAddress() {
        return demandAgentAddress;
    }

    public void setDemandAgentAddress(String demandAgentAddress) {
        this.demandAgentAddress = demandAgentAddress;
    }

    public double getMobileCoef() {
        return mobileCoef;
    }

    public void setMobileCoef(double mobileCoef) {
        this.mobileCoef = mobileCoef;
    }

    public double getOtherImps() {
        return otherImps;
    }

    public void setOtherImps(double otherImps) {
        this.otherImps = otherImps;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Long getReachImps() {
        return reachImps;
    }

    public void setReachImps(Long reachImps) {
        this.reachImps = reachImps;
    }

    public double getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(double serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public Set<MarketSegment> getTargetSegment() {
        return targetSegment;
    }

    public void setTargetSegment(Set<MarketSegment> targetSegment) {
        this.targetSegment = targetSegment;
    }

    public String getTargetSegmentName() {
        return targetSegmentName;
    }

    public void setTargetSegmentName(String targetSegmentName) {
        this.targetSegmentName = targetSegmentName;
    }

    public double getTargetedImps() {
        return targetedImps;
    }

    public void setTargetedImps(double targetedImps) {
        this.targetedImps = targetedImps;
    }

    public double getUcsLevel() {
        return ucsLevel;
    }

    public void setUcsLevel(double ucsLevel) {
        this.ucsLevel = ucsLevel;
    }

    public double getVideoCoef() {
        return videoCoef;
    }

    public void setVideoCoef(double videoCoef) {
        this.videoCoef = videoCoef;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "CampaignLogReport{" + "campaignId=" + campaignId + ", day=" + day + ", reachImps=" + reachImps + ", dayStart=" + dayStart + ", dayEnd=" + dayEnd + ", targetSegment=" + targetSegment + ", targetSegmentName=" + targetSegmentName + ", videoCoef=" + videoCoef + ", mobileCoef=" + mobileCoef + ", campaignQueries=" + campaignQueries + ", stats=" + stats + ", budget=" + budget + ", demandAgentAddress=" + demandAgentAddress + ", adxAgentAddress=" + adxAgentAddress + ", budgetMilis=" + budgetMilis + ", ucsLevel=" + ucsLevel + ", bankStatus=" + bankStatus + ", serviceLevel=" + serviceLevel + ", price=" + price + ", qualityScore=" + qualityScore + ", winner=" + winner + ", secondPrice=" + secondPrice + ", costMillis=" + costMillis + ", targetedImps=" + targetedImps + ", otherImps=" + otherImps + ", cost=" + cost + ", winning=" + winning + '}';
    }
        
}
