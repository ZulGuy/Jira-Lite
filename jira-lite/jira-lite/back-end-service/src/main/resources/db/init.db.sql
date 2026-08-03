-- 1. Створюємо нового користувача (для другого датасоурсу)
CREATE USER public_user WITH PASSWORD 'public';

-- 2. Примусово фіксуємо його лише на дефолтній схемі public
ALTER USER public_user SET search_path TO public;
GRANT CREATE ON DATABASE jira_lite TO public_user;

-- 3. МАГІЯ ДЛЯ МАЙБУТНІХ СХЕМ:
-- За замовчуванням у Postgres нові схеми доступні для ролі PUBLIC (куди входять усі юзери).
-- Ми змінюємо налаштування за замовчуванням (Default Privileges):
-- Будь-яка схема, яку в майбутньому створить ваш головний користувач (наприклад, user1 / liquibase),
-- буде ЗАБОРОНЕНА для secondary_user.
ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON SCHEMAS FROM public_user;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON TABLES FROM public_user;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON SEQUENCES FROM public_user;

-- 4. ЗАХИСТ ДЛЯ ІСНУЮЧИХ СХЕМ (якщо вони вже є в бд, окрім public)
-- Забираємо права на системну схему-шаблон (якщо раптом десь затесалася)
REVOKE ALL PRIVILEGES ON SCHEMA public FROM public_user;
-- Повертаємо права суто на роботу в public, щоб він міг там читати/писати
GRANT USAGE, CREATE ON SCHEMA public TO public_user;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO public_user;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public
GRANT USAGE, SELECT ON SEQUENCES TO public_user;
