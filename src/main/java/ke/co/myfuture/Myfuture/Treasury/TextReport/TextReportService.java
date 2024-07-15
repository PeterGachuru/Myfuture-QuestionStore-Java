package ke.co.myfuture.Myfuture.Treasury.TextReport;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TextReportService {
    @Autowired
    TextReportRepository repository;

    @Autowired
    ContributionsPlanRepository contributionsPlanRepository;

    @Autowired
    AccountRepository accountRepository;
    public UniversalResponse saveTextReport(TextReport account) {
        Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(account.planId);
        if (contributionsPlan.isEmpty())
            return null;
        account.setContributionsPlan(contributionsPlan.get());
        TextReport savedTextReport = repository.save(account);
        System.out.println(savedTextReport);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedTextReport);
        response.setStatusCode(201);
        return response;
    }

    public UniversalResponse updateTextReport(TextReport account) {
        Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(account.getPlanId());
        if (contributionsPlan.isEmpty())
            return null;
        Optional<TextReport> accountOptional = repository.findById(account.getId());
        if (accountOptional.isEmpty()) {
            return null;
        }

        accountOptional.get().update(account);

        TextReport updatedTextReport = repository.save(accountOptional.get());

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedTextReport);
        response.setStatusCode(201);
        return response;
    }

    public UniversalResponse generateReport(TextReport account) {
        TextReport savedTextReport = repository.save(account);
        System.out.println(savedTextReport);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("");
        response.setEntity(generateReportText(account));
        response.setStatusCode(201);
        return response;
    }

    private String generateReportText(TextReport textReport) {
        String report = textReport.getTemplate();
        report = attachOverallData(report, textReport.contributionsPlan.getId());
        report = attachAccounts(report, textReport.contributionsPlan.getId(), "<open-income-accounts>",
                "<close-income-accounts>", "INCOME", 0, 1, 1, 1);
        report = attachAccounts(report, textReport.contributionsPlan.getId(), "<open-uncleared-pledges-accounts>",
                "<close-uncleared-pledges-accounts>", "INCOME", 1, 0, 1, 0);
        report = attachAccounts(report, textReport.contributionsPlan.getId(), "<open-income-all-accounts>",
                "<close-income-all-accounts>", "INCOME", 1, 1, 1, 1);
        report = attachAccounts(report, textReport.contributionsPlan.getId(), "<open-pledges-accounts>",
                "<close-pledges-accounts>", "INCOME", 1, 0, 1, 1);
        report = attachAccounts(report, textReport.contributionsPlan.getId(), "<open-expense-accounts>",
                "<close-expense-accounts>", "EXPENSE", 1,1,1,1);
        return report;
    }

    private String attachOverallData(String report, Long planId) {
        System.out.println("PlanId "+planId);
        Double totalPledges = contributionsPlanRepository.totalPledges(planId);
        Double totalBudget = contributionsPlanRepository.totalBudget(planId);
        Double totalIncome = contributionsPlanRepository.totalIncome(planId);

        return report.replaceAll("\\{\\{total_pledges}}", String.valueOf(totalPledges))
                .replaceAll("\\{\\{total_money_in}}", String.valueOf(totalIncome))
                .replaceAll("\\{\\{budget}}", String.valueOf(totalBudget));
    }

    private String attachAccounts(String report, Long planId, String openingTag, String closingTag, String ownershipType, Integer allowsZeroBalance,
                                  Integer allowsZeroPledges, Integer allowsUncleared, Integer allowsCleared) {
        if (report == null || !report.contains(openingTag))
            return report;
        int startIndex = report.indexOf(openingTag);
        int endIndex = report.indexOf(closingTag);

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            String extractedString = report.substring(startIndex + openingTag.length(), endIndex);
            System.out.println("Extracted substring: " + extractedString);
            String prepend = report.substring(0, startIndex).trim()+"\n";
            String accountString = individualAccountsReport(extractedString, planId, ownershipType, allowsZeroBalance, allowsZeroPledges, allowsUncleared, allowsCleared );
            String append = report.substring(endIndex+closingTag.length());
            return attachAccounts(prepend+accountString+append, planId, openingTag, closingTag, ownershipType,  allowsZeroBalance, allowsZeroPledges, allowsUncleared, allowsCleared);
        } else {
            System.out.println("Start or end string not found, or end string appears before start string.");
            return report;
        }
    }

    private String individualAccountsReport(String extractedString, Long planId, String ownershipType, Integer allowsZeroBalance,
                                            Integer allowsZeroPledges, Integer allowsUncleared, Integer allowsCleared) {
        List<Account> accountList = accountRepository.findAllByPlanId(false, planId, ownershipType, allowsZeroBalance, allowsZeroPledges, allowsUncleared, allowsCleared);

        String accountListString = "";
        int i = 1;
        for (Account account: accountList) {
            accountListString += extractedString.replaceAll("\\{\\{numbering}}", String.valueOf(i++))
                    .replaceAll("\\{\\{full_name}}", account.getName())
                    .replaceAll("\\{\\{pledge}}", String.valueOf(account.getTargetAmount()))
                    .replaceAll("\\{\\{uncleared}}", account.getTargetAmount()-account.getBalance() > 0 ? String.valueOf(account.getTargetAmount()-account.getBalance())+"\uD83C\uDD7F\uFE0F": "")
                    .replaceAll("\\{\\{balance}}", account.getBalance() != 0.0? String.valueOf(account.getBalance())+"✅": "").trim()+"\n";
        }

        return accountListString;
    }

}

