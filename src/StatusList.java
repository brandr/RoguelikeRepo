
public class StatusList {
	
	public StatusList(){
		statuses=new Status[200];
	}
	
	public int length(){
		int length=0;
		while(length<statuses.length){
			if(statuses[length]==null)
				return length;
			length++;
		}
		return length;
	}
	
	public Status getStatus(int index){
		if(index>=0&&index<length())
			return statuses[index];
		return null;
	}
	
	public Status getStatus(String statusType) {
		int index=0;
		while(statuses[index]!=null
			&&!statuses[index].getStatusType().equals(statusType)){
			index++;
		}
		return statuses[index];
	}
	
	public boolean containsStatus(String statusType) {
		return getStatus(statusType)!=null;
	}
	
	public void addStatus(Status newStatus) {	//maybe there should be a statusList? (hashmap, possibly)
		
		if(newStatus!=null&&newStatus.getDuration()>0){
			if(newStatus.getDuration()>0){
				int index = 0;
				while(statuses[index]!=null)
					index++;
				statuses[index]=newStatus;
			}
		}
	}
	
	public void removeStatus(int index){		//will this always work? not sure.
		int length=statuses.length;
		if(index>=0&&index<length&&statuses[index]!=null){			
			while(index<length-1&&statuses[index]!= null&&statuses[index+1]!= null){
				if(index+1==length){
					statuses[index]=statuses[index+1];
					statuses[index+1]=null;
					return;
				}		
				statuses[index]=statuses[index+1];
				index++;
			}
			statuses[index]=null;
		}
	}
	
	public void removeStatus(String statusName) {
		int index=0;
		while(statuses[index]!=null&&!statuses[index].getStatusType().equals(statusName)){
			index++;
		}
		removeStatus(index);
	}
	
	public void removeStatus(Status status) {	//special function with an item as an arg instead of an int. Will probably be useful.
		int index=0;
		while(statuses[index]!=status&&statuses[index]!=null)
			index++;
		removeStatus(index);
	}
	
	public Status[] statuses;
}
