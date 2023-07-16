package com.smallworld;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;
import com.smallworld.data.*;

public class TransactionDataFetcher {
    private ObjectMapper objectMapper;
    private Transaction[] transactions;

    public TransactionDataFetcher() {
        objectMapper = new ObjectMapper();
    }

    public void fetchData() {
        try {
            File file = new File("transactions.json");
            transactions = objectMapper.readValue(file, Transaction[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getTotalTransactionAmount() {
        double totalAmount = 0.0;
        for (Transaction transaction : transactions) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }

    public double getTotalTransactionAmountSentBy(String senderFullName) {
        double totalAmount = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getSenderFullName().equals(senderFullName)) {
                totalAmount += transaction.getAmount();
            }
        }
        return totalAmount;
    }

    public double getMaxTransactionAmount() {
        double maxAmount = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > maxAmount) {
                maxAmount = transaction.getAmount();
            }
        }
        return maxAmount;
    }

    public long countUniqueClients() {
        Set<String> uniqueClients = new HashSet<>();
        for (Transaction transaction : transactions) {
            uniqueClients.add(transaction.getSenderFullName());
            uniqueClients.add(transaction.getBeneficiaryFullName());
        }
        return uniqueClients.size();
    }

    public boolean hasOpenComplianceIssues(String clientFullName) {
        for (Transaction transaction : transactions) {
            if ((transaction.getSenderFullName().equals(clientFullName) || transaction.getBeneficiaryFullName().equals(clientFullName))
                    && transaction.isIssueSolved() == false) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Transaction> getTransactionsByBeneficiaryName() {
        Map<String, Transaction> transactionsByBeneficiary = new HashMap<>();
        for (Transaction transaction : transactions) {
            transactionsByBeneficiary.put(transaction.getBeneficiaryFullName(), transaction);
        }
        return transactionsByBeneficiary;
    }

    public Set<Integer> getUnsolvedIssueIds() {
        Set<Integer> unsolvedIssueIds = new HashSet<>();
        for (Transaction transaction : transactions) {
            if (transaction.getIssueId() != null && transaction.isIssueSolved() == false) {
                unsolvedIssueIds.add(transaction.getIssueId());
            }
        }
        return unsolvedIssueIds;
    }

    public List<String> getAllSolvedIssueMessages() {
        List<String> solvedIssueMessages = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getIssueMessage() != null && transaction.isIssueSolved()) {
                solvedIssueMessages.add(transaction.getIssueMessage());
            }
        }
        return solvedIssueMessages;
    }

    public List<Transaction> getTop3TransactionsByAmount() {
        List<Transaction> sortedTransactions = Arrays.asList(transactions);
        sortedTransactions.sort(Comparator.comparingDouble(Transaction::getAmount).reversed());
        return sortedTransactions.subList(0, Math.min(sortedTransactions.size(), 3));
    }

    public Optional<String> getTopSender() {
        Map<String, Double> totalSentAmountBySender = new HashMap<>();
        for (Transaction transaction : transactions) {
            String senderFullName = transaction.getSenderFullName();
            double sentAmount = transaction.getAmount();
            totalSentAmountBySender.put(senderFullName, totalSentAmountBySender.getOrDefault(senderFullName, 0.0) + sentAmount);
        }

        return totalSentAmountBySender.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

  

    public static void main(String[] args) {
        TransactionDataFetcher dataFetcher = new  TransactionDataFetcher();
        dataFetcher.fetchData();

        // Check if transactions were fetched successfully
        if (dataFetcher.transactions != null) {
            System.out.println("Total Transaction Amount: " + dataFetcher.getTotalTransactionAmount());
            System.out.println("Total Transaction Amount Sent by Tom Shelby: " + dataFetcher.getTotalTransactionAmountSentBy("Tom Shelby"));
            System.out.println("Max Transaction Amount: " + dataFetcher.getMaxTransactionAmount());
            System.out.println("Count of Unique Clients: " + dataFetcher.countUniqueClients());
            System.out.println("Open Compliance Issues for Tom Shelby: " + dataFetcher.hasOpenComplianceIssues("Tom Shelby"));
            System.out.println("Transactions by Beneficiary Name: " + dataFetcher.getTransactionsByBeneficiaryName());
            System.out.println("Unsolved Issue IDs: " + dataFetcher.getUnsolvedIssueIds());
            System.out.println("Solved Issue Messages: " + dataFetcher.getAllSolvedIssueMessages());
            System.out.println("Top 3 Transactions by Amount: " + dataFetcher.getTop3TransactionsByAmount());
            System.out.println("Top Sender: " + dataFetcher.getTopSender().orElse("No sender found."));
        } else {
            System.out.println("Failed to fetch transactions. Please check the data source.");
        }
    }
}