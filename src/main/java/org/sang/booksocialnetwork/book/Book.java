package org.sang.booksocialnetwork.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.sang.booksocialnetwork.common.BaseEntity;
import org.sang.booksocialnetwork.history.BookTransactionHistory;
import org.sang.booksocialnetwork.user.User;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book  extends BaseEntity {

	private String title;

	private String authorName;

	private String isbn;

	private String synopsis;//bản tóm tắt nội dung sơ lược cuốn sách

	private String bookCover;//directory chua ảnh

	private boolean archived;

	private boolean shareable;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User owner;

	@OneToMany(mappedBy = "book")
	private List<Feedback> feedbacks;

	@OneToMany(mappedBy = "book")
	private List<BookTransactionHistory> histories;

	@Transient
	public double getRate(){
		if(feedbacks == null || feedbacks.isEmpty()){
			return 0.0;
		}
		var rate=this.feedbacks.stream()
				.mapToDouble(Feedback::getNote)
				.average()
				.orElse(0.0);
		//rounded ratings
		double roundedRate= (double) Math.round(rate * 10) /10;//rounded 1 chữ số sau dấu phẩy
		return roundedRate;
	}

}
