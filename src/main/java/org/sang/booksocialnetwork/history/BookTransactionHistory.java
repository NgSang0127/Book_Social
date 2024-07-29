package org.sang.booksocialnetwork.history;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.sang.booksocialnetwork.book.Book;
import org.sang.booksocialnetwork.common.BaseEntity;
import org.sang.booksocialnetwork.user.User;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookTransactionHistory extends BaseEntity {

	//user relationship
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	//book relationship
	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;
	//Nếu returned là true, điều đó có nghĩa là người mượn đã trả lại sách cho chủ sở hữu hoặc thư viện;
	// nếu false, sách vẫn đang được mượn.
	private boolean returned;
	// Nếu returnApproved là true, việc trả sách đã được phê duyệt;
    // nếu false, việc trả sách chưa được xác nhận hoặc đang chờ phê duyệt.
	private boolean returnApproved;
}
