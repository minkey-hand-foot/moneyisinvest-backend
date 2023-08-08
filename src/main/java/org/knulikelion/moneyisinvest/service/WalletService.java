package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.Wallet;
import org.knulikelion.moneyisinvest.data.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void updateUserBalances(Transaction transaction) {
        Wallet senderWallet = walletRepository.findByName(transaction.getFrom());
        Wallet recipientWallet = walletRepository.findByName(transaction.getTo());

        if (senderWallet == null) {
            senderWallet = new Wallet(null, transaction.getFrom(), 0);
            walletRepository.save(senderWallet);
        }

        if (recipientWallet == null) {
            recipientWallet = new Wallet(null, transaction.getTo(), 0);
            walletRepository.save(recipientWallet);
        }

        double senderNewBalance = senderWallet.getBalance() - transaction.getAmount();
        double recipientNewBalance = recipientWallet.getBalance() + transaction.getAmount();

        senderWallet.setBalance(senderNewBalance);
        recipientWallet.setBalance(recipientNewBalance);

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);
    }

    public Wallet findByName(String name) {
        return walletRepository.findByName(name);
    }
}
