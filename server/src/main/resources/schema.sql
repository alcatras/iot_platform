USE java2016;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(16) NOT NULL,
    salt VARCHAR(8) NOT NULL,
    password_digest VARCHAR(32) NOT NULL,

    created_at DATE,
    updated_at DATE
);

CREATE TABLE IF NOT EXISTS devices (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    user_id INTEGER NOT NULL,
    name VARCHAR(32) NOT NULL,
    type INTEGER NOT NULL,

    created_at DATE,
    updated_at DATE,

    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS sessions (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    device_id INTEGER NOT NULL,
    ip VARCHAR(15) NOT NULL,
    control_port INTEGER NOT NULL,
    valid_before DATE,

    created_at DATE,
    updated_at DATE,

    FOREIGN KEY (device_id) REFERENCES devices(id)
);

CREATE TABLE IF NOT EXISTS channels (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    user_id INTEGER NOT NULL,
    name VARCHAR(32) NOT NULL,

    created_at DATE,
    updated_at DATE,

    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS channels_devices (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    channel_id INTEGER NOT NULL,
    device_id INTEGER NOT NULL,
    can_send BIT(1) NOT NULL,
    can_receive BIT(1) NOT NULL,
    is_admin BIT(1) NOT NULL,

    created_at DATE,
    updated_at DATE,

    FOREIGN KEY (channel_id) REFERENCES channels(id),
    FOREIGN KEY (device_id) REFERENCES devices(id)
);