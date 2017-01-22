USE java2016;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(16) NOT NULL UNIQUE,
    salt VARCHAR(16) NOT NULL,
    password_digest VARCHAR(64) NOT NULL,

    created_at VARCHAR(24),
    updated_at VARCHAR(24)
);

CREATE TABLE IF NOT EXISTS devices (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    user_id INTEGER NOT NULL,
    name VARCHAR(32) NOT NULL,
    type INTEGER NOT NULL,

    created_at VARCHAR(24),
    updated_at VARCHAR(24),

    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (user_id, name)
);

CREATE TABLE IF NOT EXISTS sessions (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    device_id INTEGER NOT NULL,
    ip VARCHAR(15) NOT NULL,
    control_port INTEGER NOT NULL,
    valid_before VARCHAR(24),

    created_at VARCHAR(24),
    updated_at VARCHAR(24),

    FOREIGN KEY (device_id) REFERENCES devices(id),

    KEY (device_id, ip)
);

CREATE TABLE IF NOT EXISTS channels (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    user_id INTEGER NOT NULL,
    name VARCHAR(32) NOT NULL,

    created_at VARCHAR(24),
    updated_at VARCHAR(24),

    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS channels_devices (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,

    channel_id INTEGER NOT NULL,
    device_id INTEGER NOT NULL,
    can_send BIT(1) NOT NULL,
    can_receive BIT(1) NOT NULL,
    is_admin BIT(1) NOT NULL,

    created_at VARCHAR(24),
    updated_at VARCHAR(24),

    FOREIGN KEY (channel_id) REFERENCES channels(id),
    FOREIGN KEY (device_id) REFERENCES devices(id)
);