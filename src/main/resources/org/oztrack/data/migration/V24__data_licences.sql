create table data_licence (
    id bigserial primary key,
    title text unique not null,
    description text not null,
    info_url text not null,
    image_url text not null
);
insert into data_licence(title, description, info_url, image_url)
values(
    'Attribution',
    'This license lets others distribute, remix, tweak, and build upon your work, even commercially, as long as they credit you for the original creation. This is the most accommodating of licenses offered. Recommended for maximum dissemination and use of licensed materials.',
    'http://creativecommons.org/licenses/by/3.0',
    'http://i.creativecommons.org/l/by/3.0/88x31.png'
);
insert into data_licence(title, description, info_url, image_url)
values(
    'Attribution-ShareAlike',
    'This license lets others remix, tweak, and build upon your work even for commercial purposes, as long as they credit you and license their new creations under the identical terms. This license is often compared to “copyleft” free and open source software licenses. All new works based on yours will carry the same license, so any derivatives will also allow commercial use. This is the license used by Wikipedia, and is recommended for materials that would benefit from incorporating content from Wikipedia and similarly licensed projects.',
    'http://creativecommons.org/licenses/by-sa/3.0',
    'http://i.creativecommons.org/l/by-sa/3.0/88x31.png'
);
insert into data_licence(title, description, info_url, image_url)
values(
    'Attribution-NoDerivatives',
    'This license allows for redistribution, commercial and non-commercial, as long as it is passed along unchanged and in whole, with credit to you.',
    'http://creativecommons.org/licenses/by-nd/3.0',
    'http://i.creativecommons.org/l/by-nd/3.0/88x31.png'
);
insert into data_licence(title, description, info_url, image_url)
values(
    'Attribution-NonCommercial',
    'This licence lets others remix, tweak, and build upon your work non-commercially, and although their new works must also acknowledge you and be non-commercial, they don’t have to license their derivative works on the same terms.',
    'http://creativecommons.org/licenses/by-nc/3.0/au/deed.en',
    'http://i.creativecommons.org/l/by-nc/3.0/88x31.png'
);
insert into data_licence(title, description, info_url, image_url)
values(
    'Attribution-NonCommercial-ShareAlike',
    'This licence lets others remix, tweak, and build upon your work non-commercially, as long as they credit you and license their new creations under the identical terms.',
    'http://creativecommons.org/licenses/by-nc-sa/3.0/au/deed.en',
    'http://i.creativecommons.org/l/by-nc-sa/3.0/88x31.png'
);
insert into data_licence(title, description, info_url, image_url)
values(
    'Attribution-NonCommercial-NoDerivatives',
    'This licence is the most restrictive of our six main licences, only allowing others to download your works and share them with others as long as they credit you, but they can’t change them in any way or use them commercially.',
    'http://creativecommons.org/licenses/by-nc-nd/3.0/au/deed.en',
    'http://i.creativecommons.org/l/by-nc-nd/3.0/88x31.png'
);
insert into data_licence(title, description, info_url, image_url)
values(
    'CC0 (No Rights Reserved)',
    'CC0 enables scientists, educators, artists and other creators and owners of copyright- or database-protected content to waive those interests in their works and thereby place them as completely as possible in the public domain, so that others may freely build upon, enhance and reuse the works for any purposes without restriction under copyright or database law. Unlike the Public Domain Mark, CC0 should not be used to mark works already free of known copyright and database restrictions and in the public domain throughout the world.',
    'http://creativecommons.org/publicdomain/zero/1.0/',
    'http://i.creativecommons.org/p/zero/1.0/88x31.png'
);
insert into data_licence(title, description, info_url, image_url)
values(
    'Public Domain Mark',
    'Using the Public Domain Mark, you can mark a work that is free of known copyright restrictions and clearly convey that status. When applied properly, the PDM allows the work to be easily discovered, and provides valuable information about the work. The PDM is intended for use with old works that are free of copyright restrictions around the world, or works that have been affirmatively placed in the worldwide public domain prior to the expiration of copyright by the rights’ holder. It should not be used to mark works that are in the public domain in some jurisdictions while known to be restricted by copyright in others.',
    'http://creativecommons.org/publicdomain/mark/1.0/',
    'http://i.creativecommons.org/p/mark/1.0/88x31.png'
);

alter table project add column data_licence_id bigint references data_licence(id);