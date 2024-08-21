package ke.co.myfuture.Myfuture.Treasury.PeriodicContributionAnalysis;

import ke.co.myfuture.Myfuture.NonJdbc.Migration.DatesCalculator;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntryRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PeriodicContributionAnalysisService {

    @Autowired
    PeriodicContributionAnalysisRepository periodicContributionAnalysisRepository;

    @Autowired
    TranEntryRepository tranEntryRepository;

    @Autowired
    DatesCalculator datesCalculator;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ContributionsPlanRepository contributionsPlanRepository;

    UniversalResponse calculate(Account account) {
        ContributionsPlan contributionsPlan = account.getContributionsPlan();
        if (contributionsPlan.getTargetType().equalsIgnoreCase("monthly")) {
            return calculateMonthly(account);
        }

        return null;
    }

    UniversalResponse calculate(Long accountId) {
        System.out.println("Account Id "+accountId);
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent())
            return calculate(account.get());
        return null;
    }

    private UniversalResponse calculateMonthly(Account account) {
        Optional<PeriodicContributionAnalysis> lastAnalysis = periodicContributionAnalysisRepository.findLastForAccount(account.getId());
//        UniversalResponse universalResponse = new UniversalResponse();
        if (lastAnalysis.isPresent()) {
            System.out.println("Last analysis exists");
            Double totalUnaccountedContributions = tranEntryRepository.netCreditsForAccountAfterDate(account.getId(), datesCalculator.customDateFormat(lastAnalysis.get().creationDate, "yyyy-MM-dd HH:mm:ss.SSSSSS"));

            Date currentDate = datesCalculator.addDate(lastAnalysis.get().getCountDate(), 1, "MONTHS");
            System.out.println("currentDate: "+currentDate);
            return calculateMonthly(account, currentDate, totalUnaccountedContributions,  "MONTHS");
        } else {
            System.out.println("No existing analysis");
            Double totalUnaccountedContributions = tranEntryRepository.netCreditsForAccountAfterDate(account.getId(), "1970-01-01 00:20:13.559000");

            Date currentDate = account.getStartDate();
            System.out.println("account.getStartDate(): "+account.getStartDate());
            return calculateMonthly(account, currentDate, totalUnaccountedContributions,  "MONTHS");
        }
    }

    UniversalResponse calculateMonthly(Account account, Date currentDate,  Double totalUnaccountedContributions, String durationId) {
        UniversalResponse universalResponse = new UniversalResponse();
        System.out.println("totalUnaccountedContributions: "+totalUnaccountedContributions);
        if (totalUnaccountedContributions == null) {
            System.out.println("totalUnaccountedContributions is null");
            universalResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            universalResponse.setMessage("Already calculated");
            return universalResponse;
        }
        if (currentDate == null) {
            System.out.println("Date is null");
            universalResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            universalResponse.setMessage("Null start date");
            return universalResponse;
        }

        Double amountForPeriod = 0.0;
        while (totalUnaccountedContributions > 0 && currentDate.before(account.getContributionsPlan().getDeadlineDate())) {
            if (totalUnaccountedContributions > account.getTargetAmount())
                amountForPeriod = account.getTargetAmount();
            else
                amountForPeriod = totalUnaccountedContributions;
            totalUnaccountedContributions -= amountForPeriod;

            PeriodicContributionAnalysis periodicContributionAnalysis = new PeriodicContributionAnalysis();
            periodicContributionAnalysis.setAccount(account);
            periodicContributionAnalysis.setAmount(amountForPeriod);
            periodicContributionAnalysis.setCountDate(currentDate);
            currentDate = datesCalculator.addDate(currentDate, 1, durationId);
            periodicContributionAnalysisRepository.save(periodicContributionAnalysis);
        }
        universalResponse.setStatusCode(HttpStatus.OK.value());
        universalResponse.setMessage("Calculated successfully");
        return universalResponse;
    }

    public String toHtmlReport(Long planId) {
        System.out.println("Plan Id: "+planId);
        System.out.println("TO html");
        Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(planId);
        if (contributionsPlan.isEmpty())
            return "";
        String titling = "<h1>"+contributionsPlan.get().getName()+"</h1><h2>Group Name Withheld</h2>";
        List<String> months = periodicContributionAnalysisRepository.allRelevantMonths(planId);
        System.out.println(Arrays.deepToString(months.toArray()));

        StringBuilder tableHeaders = new StringBuilder();

        tableHeaders.append("<th style=\"border: 1px solid black; padding: 2px\">").append("No.").append("</th>");
        tableHeaders.append("<th style=\"border: 1px solid black; padding: 2px\">").append("Name").append("</th>");
        for (String month: months) {
            tableHeaders.append("<th style=\"border: 1px solid black; padding: 2px\">").append(month).append("</th>");
        }

        List<PeriodicContributionAnalysisRepository.SummaryPeriod> summaryPeriodList = periodicContributionAnalysisRepository.getAllForPlan(planId);
        List<AccountPeriodsMapping> accountPeriodsMappings = convert(summaryPeriodList);
        int no = 0;

        String value;
        StringBuilder rows = new StringBuilder();
        System.out.println("Count of accountPeriodsMapping: "+accountPeriodsMappings.size());
        for (AccountPeriodsMapping accountPeriodsMapping: accountPeriodsMappings) {
            System.out.println(accountPeriodsMapping);
            no++;
            StringBuilder row;
            row = new StringBuilder("<td style=\"border: 1px solid black; padding: 2px\">" + no + "</td>");
            row.append("<td style=\"border: 1px solid black; padding: 2px\">").append(accountPeriodsMapping.miniAccount.getName()).append("</td>");
            for (String month: months) {
                if (accountPeriodsMapping.periodAmounts.containsKey(month)) {
                    row.append("<td style=\"border: 1px solid black; padding: 2px\">").append(String.format("%.0f", accountPeriodsMapping.periodAmounts.get(month))).append("</td>");
                }else {
                    row.append("<td style=\"border: 1px solid black; padding: 2px\">-</td>");
                }
            }
            rows.append("<tr style=\"border: 1px solid black; padding: 2px\">").append(row).append("</tr>").append("\n");
        }

        String combined = "<table  style=\"border-collapse: collapse; border-spacing: 0;\">\n "+titling+" \n<tr>"+tableHeaders+"</tr>\n"+rows+"\n</table>";
        System.out.println(combined);
        return combined;
    }

    private List<AccountPeriodsMapping> convert(List<PeriodicContributionAnalysisRepository.SummaryPeriod> summaryPeriodList ) {
        List<AccountPeriodsMapping> accountPeriodsMappings = new ArrayList<>();

        AccountPeriodsMapping accountPeriodsMapping = null;

        for (PeriodicContributionAnalysisRepository.SummaryPeriod summaryPeriod: summaryPeriodList) {
            System.out.println("Id: "+summaryPeriod.getAccountId());
            if (accountPeriodsMapping == null
                    || !Objects.equals(accountPeriodsMapping.miniAccount.id, summaryPeriod.getAccountId())) {
                System.out.println("Next record");
                if (accountPeriodsMapping != null) {
                    System.out.println("is not null");
                    System.out.println(accountPeriodsMapping);
                    accountPeriodsMappings.add(accountPeriodsMapping);

                }else {

                }
                accountPeriodsMapping = new AccountPeriodsMapping();
                MiniAccount miniAccount = new MiniAccount();
                miniAccount.id = summaryPeriod.getAccountId();
                miniAccount.name = summaryPeriod.getName();
                accountPeriodsMapping.miniAccount = miniAccount;
                accountPeriodsMapping.periodAmounts = new HashMap<>();
            }else {
                System.out.println("Same record");
            }
            accountPeriodsMapping.periodAmounts.put(summaryPeriod.getPeriod(), summaryPeriod.getAmount());
        }

        if (accountPeriodsMapping != null)
            accountPeriodsMappings.add(accountPeriodsMapping);

        System.out.println("Size of list: "+accountPeriodsMappings.size());

        return accountPeriodsMappings;
    }

    @Data
    private class AccountPeriodsMapping {
        MiniAccount miniAccount;
        HashMap<String, Double> periodAmounts;
    }

    @Data
    private class MiniAccount {
        Long id;
        String name;
    }
}
