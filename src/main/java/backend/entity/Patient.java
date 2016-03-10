package backend.entity;

import java.util.Date;

/**
 * Created by user on 07.03.2016.
 */
public class Patient {

	String name;


	String surname;
	String patronymic;
	Date birthday;
	int case_history_num;
	String diagnosis;


	public Patient()
	{
		super();
	}

	public Patient(String name, String patronymic, String surname,int case_history_num,String diagnosis,Date birthday) {
		super();
		this.name = name;
		this.patronymic = patronymic;
		this.surname = surname;
		this.birthday = birthday;
		this.case_history_num = case_history_num;
		this.diagnosis = diagnosis;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPatronymic() {
		return patronymic;
	}

	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	 public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
	this.birthday = birthday;
	}

	public int getCase_history_num() {
		return case_history_num;
	}

	public void setCase_history_num(int case_history_num) {
		this.case_history_num = case_history_num;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}



	@Override
	public String toString() {
		return "Patient{" +
			"birthday=" + birthday +
			", name='" + name + '\'' +
			", surname='" + surname + '\'' +
			", patronymic='" + patronymic + '\'' +
			", case_history_num=" + case_history_num +
			", diagnosis='" + diagnosis + '\'' +
			'}';
	}




}
