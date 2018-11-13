package org.nyu.crypto.dto;



import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="messages")
public class Message {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "message")
    private String message;

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }
}
