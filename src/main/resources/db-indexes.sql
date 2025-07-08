-- Database Performance Optimization Indexes
-- Bu SQL dosyasını database'inizde çalıştırarak performansı artırabilirsiniz

-- Orders tablosu için indexler
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_delivery_date ON orders(delivery_date);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);
CREATE INDEX IF NOT EXISTS idx_orders_estimated_delivery ON orders(estimated_delivery_date);

-- Compound index for common queries
CREATE INDEX IF NOT EXISTS idx_orders_customer_status ON orders(customer_id, status);
CREATE INDEX IF NOT EXISTS idx_orders_date_status ON orders(order_date, status);

-- Customers tablosu için indexler
CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(first_name, last_name);
CREATE INDEX IF NOT EXISTS idx_customers_phone ON customers(phone);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);

-- Measurements tablosu için indexler
CREATE INDEX IF NOT EXISTS idx_measurements_customer_id ON measurements(customer_id);

-- Messages tablosu için indexler
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at);
CREATE INDEX IF NOT EXISTS idx_messages_is_read ON messages(is_read);
CREATE INDEX IF NOT EXISTS idx_messages_read_created ON messages(is_read, created_at);

-- Blog/Post tablosu için indexler
CREATE INDEX IF NOT EXISTS idx_posts_published ON posts(published);
CREATE INDEX IF NOT EXISTS idx_posts_created_at ON posts(created_at);
CREATE INDEX IF NOT EXISTS idx_posts_url_slug ON posts(url_slug);
CREATE INDEX IF NOT EXISTS idx_posts_published_created ON posts(published, created_at);

CREATE INDEX IF NOT EXISTS idx_blogs_published ON blog(published);
CREATE INDEX IF NOT EXISTS idx_blogs_created_at ON blog(created_at);
CREATE INDEX IF NOT EXISTS idx_blogs_slug ON blog(slug);

-- Users tablosu için indexler
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_is_approved ON users(is_approved);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Admin tablosu için indexler
CREATE INDEX IF NOT EXISTS idx_admin_role ON admin(role);

-- İstatistikler için view'lar
CREATE OR REPLACE VIEW order_statistics AS
SELECT 
    DATE_TRUNC('month', order_date) as month,
    COUNT(*) as total_orders,
    SUM(total_price) as total_revenue,
    AVG(total_price) as avg_order_value,
    status
FROM orders 
WHERE total_price IS NOT NULL
GROUP BY DATE_TRUNC('month', order_date), status;

CREATE OR REPLACE VIEW customer_statistics AS
SELECT 
    c.id,
    c.first_name,
    c.last_name,
    COUNT(o.id) as total_orders,
    SUM(o.total_price) as total_spent,
    AVG(o.total_price) as avg_order_value,
    MAX(o.order_date) as last_order_date
FROM customers c
LEFT JOIN orders o ON c.id = o.customer_id
GROUP BY c.id, c.first_name, c.last_name;

-- Performance monitoring queries
-- En yavaş sorgular için:
-- SELECT query, mean_exec_time, calls FROM pg_stat_statements ORDER BY mean_exec_time DESC LIMIT 10; 