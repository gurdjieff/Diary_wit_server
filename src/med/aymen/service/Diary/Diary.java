package med.aymen.service.Diary;

import javax.xml.bind.annotation.XmlRootElement;

	@XmlRootElement(name = "projectdb")
	public class Diary { 
	
		public int id;
		public String user_name;
		public String Diary_title;
		public String Diary_text;
		
		public Diary() {
			this.user_name = "medaymen";
			this.Diary_title = "testing title";
			this.Diary_text = "testing diary text";
		}
		
		public Diary(String username,String password) {
			super();
			this.user_name = "medaymen";
			this.Diary_title = Diary_title;
			this.Diary_text = Diary_text;
		}

		@Override
		public String toString() {
			return "Diary [id=" + id + ", user_name=" + user_name + ", Diary_title=" + Diary_title + ", Diary_text=" + Diary_text + "]";
		}
		
		public void setDiary(String user_name, String Diary_title,String Diary_text ) {
		    this.user_name = user_name;
			this.Diary_title = Diary_title;
		    this.Diary_text = Diary_text;
		    
		  }

	}

