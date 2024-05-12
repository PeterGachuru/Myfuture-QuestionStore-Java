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
        report = attachAccounts(report, textReport.contributionsPlan.getId(), "<open-income-accounts>", "<close-income-accounts>", "INCOME");
        report = attachAccounts(report, textReport.contributionsPlan.getId(), "<open-expense-accounts>", "<close-expense-accounts>", "EXPENSE");
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

    private String attachAccounts(String report, Long planId, String openingTag, String closingTag, String ownershipType) {
        if (report == null || !report.contains(openingTag))
            return report;
        int startIndex = report.indexOf(openingTag);
        int endIndex = report.indexOf(closingTag);

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            String extractedString = report.substring(startIndex + openingTag.length(), endIndex);
            System.out.println("Extracted substring: " + extractedString);
            String prepend = report.substring(0, startIndex).trim();
            String accountString = individualAccountsReport(extractedString, planId, ownershipType);
            String append = report.substring(endIndex+closingTag.length());
            return attachAccounts(prepend+accountString+append, planId, openingTag, closingTag, ownershipType);
        } else {
            System.out.println("Start or end string not found, or end string appears before start string.");
            return report;
        }
    }

    private String individualAccountsReport(String extractedString, Long planId, String ownershipType) {
        List<Account> accountList = accountRepository.findAllByPlanId(false, planId, ownershipType);

        String accountListString = "";
        int i = 1;
        for (Account account: accountList) {
            accountListString += extractedString.replaceAll("\\{\\{numbering}}", String.valueOf(i++))
                    .replaceAll("\\{\\{full_name}}", account.getName())
                    .replaceAll("\\{\\{balance}}", String.valueOf(account.getBalance()))+"\n";
        }

        return accountListString;
    }

}

