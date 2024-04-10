package ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry;

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
}
