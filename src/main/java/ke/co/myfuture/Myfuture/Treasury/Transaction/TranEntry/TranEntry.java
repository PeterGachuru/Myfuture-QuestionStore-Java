package ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Transaction.Transaction;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class TranEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TranType tranType;

    @Column(nullable = false)
    private String particulars;

    @Column(nullable = false)
    Long accountId;

    @Column(nullable = false)
    private String accountName;

    @Transient
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tran_id")
    Transaction transaction;


}
