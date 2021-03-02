package util.exception;

public class RobotInterruptException extends Exception{

    public RobotInterruptException(){

    }
   public RobotInterruptException(String msg){
        super(msg);
    }

    public String toString(){
        return " Sequence terminated";
    }
}
