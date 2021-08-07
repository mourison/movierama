package gr.tsamtsouris.movierama.enums;

public enum ReactionEnum {

     LIKE("like"),
     HATE("hate");

     private final String value;

     private ReactionEnum(String value){
         this.value = value;
     }

    public String getValue() {
        return value;
    }
}
