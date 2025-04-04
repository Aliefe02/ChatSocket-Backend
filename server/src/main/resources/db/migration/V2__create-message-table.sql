DROP TABLE IF EXISTS message;

CREATE TABLE message (
    id CHAR(36) NOT NULL,
    sender_id CHAR(36) NOT NULL,
    receiver_id CHAR(36) NOT NULL,
    message TEXT NOT NULL,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES user(id) ON DELETE CASCADE
);
