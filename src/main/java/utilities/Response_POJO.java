package utilities;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response_POJO {

	private String url;
	private String services;
	private String expertise;
	private Courses courses;
	private String instructor;
	private String Linkedin;
}

@Getter
@Setter
class Courses {

	private List<Child> webAutomation;
	private List<Child> api;
	private List<Child> mobile;
}

@Getter
@Setter
class Child {

	private String courseTitle;
	private String price;
}
//Delete this comment