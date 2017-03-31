package net.sf.javascribe.example1.domain;

import net.sf.javascribe.patterns.handwritten.BusinessObject;
import net.sf.javascribe.patterns.handwritten.BusinessRule;

@BusinessObject(group="DomainServices",name="BookDomainService")
public class BookDomainService {
	
	@BusinessRule
	public void addBook(String title,Integer authorId,Integer publisherId) {
		
	}

}
