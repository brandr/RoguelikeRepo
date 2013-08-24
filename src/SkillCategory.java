
public class SkillCategory {

	public SkillCategory(String category) {
		setSkillNames(category);
	}
	
	public String toString(){
		return categoryName;
	}
	
	public String[] skillNames(){
		String[] names=new String[skills.length];
		for(int i=0;i<skills.length;i++){
			names[i]=getSkill(i).toString();
		}
		return names;
	}
	
	public void setSkillNames(String category){
		categoryName=category;
		
		int index=0;
		String[] categories=Skill.CATEGORIES;
		while(index<categories.length&&category!=categories[index])
			index++;
		if(category==categories[index]){
			setSkills(Skill.ALL_SKILLS[index]);
			return;
		}
		System.out.println("SKILL SETUP ERROR");	//TODO: need error messages that don't go to console.
	}
	
	public void setSkills(String[] skillNames){
		skills=new Skill[skillNames.length];
		for(int i=0;i<skills.length;i++){
			skills[i]=new Skill(skillNames[i],categoryName);
		}
	}
	
	public void addSkill(Skill skill) {		//only used for overwriting default, level 0 skills during character creation.
		for(int i=0;i<skills.length&&skills[i]!=null;i++){
			if(skill.toString().equals(skills[i].toString()))
				skills[i]=new Skill(skill);
		}
	}
	
	public Skill[] getSkills(){
		return skills;
	}
	
	public Skill getSkill(int index){
		if(index>=0&&index<skills.length)
			return skills[index];
		return null;
	}

	private String categoryName;
	private Skill[] skills;
	
}
