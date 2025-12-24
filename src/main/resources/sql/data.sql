INSERT INTO app_user (id, username, password, role, account_non_locked, failed_attempts)
VALUES (gen_random_uuid(), 'user1', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'USER', true, 0),
       (gen_random_uuid(), 'user2', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'USER', true, 0),
       (gen_random_uuid(), 'user3', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'USER', true, 0),
       (gen_random_uuid(), 'user4', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'USER', true, 0),
       (gen_random_uuid(), 'user5', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'USER', true, 0),

       (gen_random_uuid(), 'moder1', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'MODERATOR', true, 0),
       (gen_random_uuid(), 'moder2', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'MODERATOR', true, 0),

       (gen_random_uuid(), 'admin1', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'SUPER_ADMIN', true, 0),
       (gen_random_uuid(), 'admin2', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'SUPER_ADMIN', true, 0),
       (gen_random_uuid(), 'admin3', '$2a$10$aGC/go9pxYuIFNS/kSL6fuiDjNIB8iCCqwkaWBB1c.PTRTVpa1J6.',
        'SUPER_ADMIN', true, 0)
ON CONFLICT (username) DO NOTHING;