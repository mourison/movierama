package gr.tsamtsouris.movierama.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "REACTIONS")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reactionId;

    @Column
    private Boolean isLike;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "MOVIE_ID")
    private Movie movie;

}
