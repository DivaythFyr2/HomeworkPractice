INSERT INTO app_user (id, provider, provider_user_id, name, email, role)
VALUES (gen_random_uuid(), 'GOOGLE', 'test-sub-1', 'test-user', 'test1@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-2', 'test-user-2', 'test2@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-3', 'test-user-3', 'test3@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-4', 'test-user-4', 'test4@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-5', 'test-user-5', 'test5@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-6', 'test-user-6', 'test6@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-7', 'test-user-7', 'test7@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-8', 'test-user-8', 'test8@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-9', 'test-user-9', 'test9@example.com', 'USER'),
       (gen_random_uuid(), 'GOOGLE', 'test-sub-10', 'test-user-10', 'admin@example.com', 'ADMIN');