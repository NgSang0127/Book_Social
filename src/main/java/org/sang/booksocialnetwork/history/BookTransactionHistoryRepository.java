package org.sang.booksocialnetwork.history;

import java.util.List;
import java.util.Optional;
import org.sang.booksocialnetwork.book.Book;
import org.sang.booksocialnetwork.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

	@Query("""
			SELECT history
			FROM BookTransactionHistory history
			WHERE history.user.id =:userId
			""")
	Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, @Param("userId") Integer userId);

	@Query("""
			SELECT history
			FROM BookTransactionHistory history
			WHERE history.book.owner.id =:userId
			""")
	Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

	@Query("""
			SELECT
			(COUNT(*) >0 ) AS isBorrowed
			FROM BookTransactionHistory bookTransactionHistory
			WHERE bookTransactionHistory.user.id =: userId
			AND bookTransactionHistory.book.id =: bookId
			AND bookTransactionHistory.returnApproved =false
			""")
	/*
	Phương thức isAlreadyBorrowedByUser trả về một giá trị boolean:
	True: Nếu người dùng (userId) đã mượn cuốn sách (bookId) và chưa trả lại (returnApproved = false).
	False: Nếu người dùng chưa mượn cuốn sách này, hoặc đã mượn nhưng đã trả lại.
	 */
	boolean isAlreadyBorrowedByUser(Integer bookId, @Param("userId") Integer userId);

	@Query("""
			SELECT transaction
			FROM BookTransactionHistory transaction
			WHERE transaction.user.id =: userId
			AND transaction.book.id =: bookId
			AND transaction.returned= false
			AND transaction.returnApproved =false
			""")
	Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

	@Query("""
			SELECT transaction
			FROM BookTransactionHistory transaction
			WHERE transaction.book.owner.id =: ownerId
			AND transaction.book.id =: bookId
			AND transaction.returned= true
			AND transaction.returnApproved =false
			""")
	Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer ownerId);
}
