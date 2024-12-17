package code.repository;

import code.model.more.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
  Page<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

  @Query("SELECT SUM(t.transferAmount) " +
      "FROM Transaction t " +
      "WHERE FUNCTION('MONTH', t.transactionDate) = :month " +
      "AND FUNCTION('YEAR', t.transactionDate) = :year " +
      "AND t.content LIKE CONCAT('%SEVQR_0', :x, '_%')")
  Long getTotalAmountByMonthYearAndX(@Param("month") int month,
      @Param("year") int year,
      @Param("x") int x);
}
