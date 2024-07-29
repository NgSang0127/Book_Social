package org.sang.booksocialnetwork.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

	public static Specification<Book> withOwnerId(Integer ownerId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
	}
	/*
	root: đại diện cho thực thể Book trong truy vấn.
    query: đại diện cho truy vấn đang được thực thi, nó có thể được sử dụng để tùy chỉnh cách kết quả được trả về.
    criteriaBuilder: cung cấp các phương thức để xây dựng các biểu thức tiêu chí, như equal, like, greaterThan, v.v.
	 */

}
