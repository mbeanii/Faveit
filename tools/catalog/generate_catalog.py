#!/usr/bin/env python3
"""Generate Faveit's bundled, tagged, local-first master catalog.

The grouped source below is intentionally hand-curated. Group membership supplies
transparent content features for the on-device nearest-neighbor recommender; no
descriptions, artwork, user data, network calls, or external model are involved.
"""

from __future__ import annotations

import json
import re
import unicodedata
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / "app/src/main/assets/catalog.json"


def group(facet: str, tags: str, emoji: str, names: str) -> tuple[str, list[str], str, list[str]]:
    return facet, tags.split(","), emoji, [name.strip() for name in names.split(";")]


CATEGORIES = {
    "restaurants": [
        group("burger chains", "burger,quick service,american", "🍔", "McDonald's;Burger King;Wendy's;In-N-Out;Five Guys;Shake Shack;Whataburger;Culver's;Sonic Drive-In;Jack in the Box"),
        group("chicken chains", "chicken,quick service,american", "🍗", "Chick-fil-A;KFC;Popeyes;Raising Cane's;Wingstop;Zaxby's;Bojangles;Church's Texas Chicken;Jollibee;El Pollo Loco"),
        group("pizza", "pizza,casual,italian american", "🍕", "Domino's;Pizza Hut;Papa Johns;Little Caesars;Marco's Pizza;MOD Pizza;Blaze Pizza;California Pizza Kitchen;Mellow Mushroom;Round Table Pizza"),
        group("sandwiches", "sandwich,quick service,lunch", "🥪", "Subway;Jersey Mike's;Jimmy John's;Firehouse Subs;Arby's;Panera Bread;Potbelly;Schlotzsky's;Which Wich;Capriotti's"),
        group("coffee shops", "coffee,cafe,breakfast", "☕", "Starbucks;Dunkin';Dutch Bros;Tim Hortons;Peet's Coffee;The Coffee Bean & Tea Leaf;Caribou Coffee;Scooter's Coffee;Blue Bottle Coffee;Philz Coffee"),
        group("mexican quick service", "mexican,tex mex,quick service", "🌮", "Taco Bell;Chipotle;QDOBA;Del Taco;Moe's Southwest Grill;Torchy's Tacos;Taco Cabana;Baja Fresh;Cafe Rio;Costa Vida"),
        group("asian casual", "asian,casual,shared plates", "🥡", "Panda Express;P.F. Chang's;Pei Wei;Noodles & Company;Yoshinoya;Teriyaki Madness;Bonchon;bb.q Chicken;Din Tai Fung;Wagamama"),
        group("salads and bowls", "salad,bowl,fast casual", "🥗", "CAVA;Sweetgreen;Chopt;Just Salad;Freshii;Veggie Grill;Flower Child;CoreLife Eatery;Clean Eatz;Tender Greens"),
        group("breakfast", "breakfast,brunch,diner", "🥞", "IHOP;Denny's;Waffle House;First Watch;Cracker Barrel;Perkins;Bob Evans;Snooze A.M. Eatery;Another Broken Egg Cafe;Huddle House"),
        group("american casual", "american,casual,group dining", "🍽️", "Applebee's;Chili's;TGI Fridays;Red Robin;BJ's Restaurant & Brewhouse;The Cheesecake Factory;Yard House;Buffalo Wild Wings;Dave & Buster's;Cheddar's Scratch Kitchen"),
        group("steakhouses", "steak,upscale,american", "🥩", "Texas Roadhouse;Outback Steakhouse;LongHorn Steakhouse;Ruth's Chris Steak House;The Capital Grille;Fleming's;Morton's The Steakhouse;Fogo de Chão;STK Steakhouse;Smith & Wollensky"),
        group("italian", "italian,pasta,casual", "🍝", "Olive Garden;Carrabba's Italian Grill;Maggiano's Little Italy;Buca di Beppo;North Italia;Brio Italian Grille;Fazoli's;Romano's Macaroni Grill;Eataly;The Old Spaghetti Factory"),
        group("seafood", "seafood,fish,upscale casual", "🦞", "Red Lobster;Bonefish Grill;Joe's Crab Shack;Legal Sea Foods;McCormick & Schmick's;Bubba Gump Shrimp Co.;Eddie V's;Ocean Prime;Chart House;King's Fish House"),
        group("barbecue", "barbecue,smoked meat,american", "🍖", "Famous Dave's;Dickey's Barbecue Pit;Mission BBQ;Sonny's BBQ;City Barbeque;Rudy's Country Store and Bar-B-Q;Smokey Bones;Dinosaur Bar-B-Que;Franklin Barbecue;Joe's Kansas City Bar-B-Que"),
        group("dessert shops", "dessert,ice cream,bakery", "🍦", "Dairy Queen;Baskin-Robbins;Cold Stone Creamery;Krispy Kreme;Cinnabon;Crumbl;Nothing Bundt Cakes;Insomnia Cookies;Auntie Anne's;Jamba"),
        group("bakeries and cafes", "bakery,cafe,light meal", "🥐", "Paris Baguette;Tous les Jours;Le Pain Quotidien;Corner Bakery Cafe;Au Bon Pain;Pret A Manger;85°C Bakery Cafe;Einstein Bros. Bagels;Great Harvest Bread Co.;La Madeleine"),
        group("destination dining", "fine dining,tasting menu,destination", "💎", "Eleven Madison Park;The French Laundry;Per Se;Alinea;The Inn at Little Washington;Le Bernardin;Gramercy Tavern;Blue Hill at Stone Barns;Commander's Palace;Canlis"),
        group("global destination dining", "fine dining,international,destination", "🌍", "Noma;Osteria Francescana;Central;Maido;Disfrutar;Asador Etxebarri;Gaggan Anand;Quintonil;Pujol;Mirazur"),
        group("american regional icons", "regional,classic,american", "🗺️", "Katz's Delicatessen;Peter Luger Steak House;Joe's Stone Crab;Musso & Frank Grill;Antoine's;Galatoire's;The Varsity;Philippe The Original;Primanti Bros.;St. Elmo Steak House"),
        group("japanese dining", "japanese,sushi,ramen", "🍣", "Nobu;Benihana;Kura Sushi;Sugarfish;RA Sushi;Genki Sushi;Gyu-Kaku;Ramen Tatsu-Ya;Ippudo;Ichiran"),
        group("drinks and snack shops", "drinks,boba,snack", "🧋", "Swig;Gong cha;Kung Fu Tea;Sharetea;Chatime;Happy Lemon;7 Brew Coffee;The Human Bean;Playa Bowls;Wetzel's Pretzels"),
    ],
    "music": [
        group("current pop", "pop,current,vocal", "🎤", "Taylor Swift;Billie Eilish;Sabrina Carpenter;Olivia Rodrigo;Ariana Grande;Dua Lipa;Chappell Roan;Tate McRae;Benson Boone;Gracie Abrams"),
        group("pop icons", "pop,iconic,vocal", "✨", "Beyoncé;Lady Gaga;Bruno Mars;Rihanna;Justin Bieber;Katy Perry;Adele;Ed Sheeran;Miley Cyrus;Harry Styles"),
        group("current hip hop", "hip hop,rap,current", "🎧", "Kendrick Lamar;Drake;Travis Scott;Tyler, the Creator;J. Cole;Doja Cat;Nicki Minaj;Future;Megan Thee Stallion;21 Savage"),
        group("classic hip hop", "hip hop,rap,classic", "🎙️", "Tupac Shakur;The Notorious B.I.G.;Jay-Z;Nas;Eminem;Wu-Tang Clan;A Tribe Called Quest;Outkast;Missy Elliott;Lauryn Hill"),
        group("modern r and b", "r&b,soul,current", "💜", "SZA;The Weeknd;Frank Ocean;H.E.R.;Summer Walker;Daniel Caesar;Jhené Aiko;Giveon;Victoria Monét;Leon Bridges"),
        group("soul legends", "soul,r&b,classic", "🎶", "Aretha Franklin;Stevie Wonder;Marvin Gaye;Prince;Whitney Houston;Diana Ross;Al Green;Otis Redding;Etta James;Sam Cooke"),
        group("classic rock", "rock,classic,guitar", "🎸", "The Beatles;Fleetwood Mac;Queen;Led Zeppelin;The Rolling Stones;Pink Floyd;Eagles;The Who;Creedence Clearwater Revival;Aerosmith"),
        group("alternative rock", "rock,alternative,90s", "⚡", "Nirvana;Radiohead;Pearl Jam;Foo Fighters;Red Hot Chili Peppers;The Smashing Pumpkins;Soundgarden;Alice in Chains;Weezer;R.E.M."),
        group("indie", "indie,alternative,current", "🌙", "Arctic Monkeys;Lana Del Rey;Tame Impala;Vampire Weekend;Phoebe Bridgers;Mitski;The National;Bon Iver;Florence + the Machine;The 1975"),
        group("metal", "metal,hard rock,guitar", "🤘", "Metallica;Black Sabbath;Iron Maiden;AC/DC;Guns N' Roses;Tool;System of a Down;Slipknot;Judas Priest;Megadeth"),
        group("modern country", "country,current,americana", "🤠", "Morgan Wallen;Luke Combs;Zach Bryan;Chris Stapleton;Kacey Musgraves;Lainey Wilson;Jelly Roll;Megan Moroney;Thomas Rhett;Shaboozey"),
        group("country legends", "country,classic,americana", "🪕", "Dolly Parton;Johnny Cash;Willie Nelson;Patsy Cline;George Strait;Reba McEntire;Garth Brooks;Shania Twain;Hank Williams;Loretta Lynn"),
        group("electronic", "electronic,dance,dj", "🪩", "Daft Punk;Calvin Harris;Avicii;Skrillex;Tiësto;deadmau5;The Chemical Brothers;Disclosure;Fred again..;ODESZA"),
        group("latin", "latin,reggaeton,spanish", "🔥", "Bad Bunny;Shakira;Karol G;J Balvin;Daddy Yankee;Selena;Luis Miguel;Juanes;Rosalía;Rauw Alejandro"),
        group("k pop", "k-pop,korean,pop", "🌟", "BTS;BLACKPINK;TWICE;Stray Kids;SEVENTEEN;NewJeans;EXO;Red Velvet;ATEEZ;LE SSERAFIM"),
        group("jazz", "jazz,instrumental,classic", "🎷", "Miles Davis;John Coltrane;Ella Fitzgerald;Louis Armstrong;Billie Holiday;Duke Ellington;Thelonious Monk;Charles Mingus;Chet Baker;Herbie Hancock"),
        group("classical", "classical,orchestral,composer", "🎻", "Ludwig van Beethoven;Wolfgang Amadeus Mozart;Johann Sebastian Bach;Pyotr Ilyich Tchaikovsky;Frédéric Chopin;Antonio Vivaldi;Claude Debussy;Gustav Mahler;Igor Stravinsky;Sergei Rachmaninoff"),
        group("folk and songwriters", "folk,singer songwriter,acoustic", "🪶", "Bob Dylan;Joni Mitchell;Carole King;James Taylor;Tracy Chapman;Paul Simon;Joan Baez;Leonard Cohen;Brandi Carlile;Noah Kahan"),
        group("reggae and global", "reggae,world,global", "🌍", "Bob Marley & The Wailers;Burning Spear;Toots and the Maytals;Peter Tosh;Buena Vista Social Club;Fela Kuti;Cesária Évora;Youssou N'Dour;Ali Farka Touré;Angélique Kidjo"),
        group("funk and disco", "funk,disco,dance", "🕺", "Earth, Wind & Fire;ABBA;Bee Gees;Chic;Donna Summer;Parliament-Funkadelic;Kool & the Gang;The Isley Brothers;Sly and the Family Stone;Gloria Gaynor"),
        group("blues", "blues,roots,guitar", "🎺", "B.B. King;Muddy Waters;Robert Johnson;Howlin' Wolf;Buddy Guy;Koko Taylor;John Lee Hooker;Stevie Ray Vaughan;Gary Clark Jr.;Susan Tedeschi"),
    ],
    "movies": [
        group("animation", "animation,family,adventure", "🧸", "Toy Story;Spirited Away;The Lion King;Spider-Man: Into the Spider-Verse;Finding Nemo;Shrek;Up;The Incredibles;Coco;How to Train Your Dragon"),
        group("superhero", "superhero,action,comic", "🦸", "The Dark Knight;Avengers: Endgame;Black Panther;Spider-Man 2;Iron Man;The Avengers;Wonder Woman;Guardians of the Galaxy;Logan;The Batman"),
        group("science fiction", "science fiction,future,space", "🚀", "Star Wars: A New Hope;The Matrix;2001: A Space Odyssey;Blade Runner;Interstellar;Alien;Arrival;Back to the Future;Terminator 2: Judgment Day;Dune: Part Two"),
        group("fantasy", "fantasy,magic,adventure", "🧙", "The Lord of the Rings: The Fellowship of the Ring;The Lord of the Rings: The Return of the King;Harry Potter and the Sorcerer's Stone;The Princess Bride;Pan's Labyrinth;The Wizard of Oz;Stardust;The NeverEnding Story;Excalibur;The Green Knight"),
        group("action adventure", "action,adventure,thriller", "💥", "Raiders of the Lost Ark;Mad Max: Fury Road;Top Gun: Maverick;Jurassic Park;Mission: Impossible - Fallout;Die Hard;Gladiator;Pirates of the Caribbean: The Curse of the Black Pearl;The Bourne Identity;Crouching Tiger, Hidden Dragon"),
        group("comedies", "comedy,funny,ensemble", "😂", "Some Like It Hot;Groundhog Day;Bridesmaids;The Big Lebowski;Mean Girls;Superbad;Monty Python and the Holy Grail;Ferris Bueller's Day Off;Coming to America;Booksmart"),
        group("romance", "romance,relationship,drama", "❤️", "When Harry Met Sally...;Pride & Prejudice;The Notebook;Before Sunrise;Roman Holiday;Crazy Rich Asians;Titanic;Moonstruck;Past Lives;Portrait of a Lady on Fire"),
        group("crime", "crime,gangster,thriller", "🕵️", "The Godfather;Goodfellas;Pulp Fiction;The Departed;Heat;The Silence of the Lambs;L.A. Confidential;No Country for Old Men;The Usual Suspects;Memories of Murder"),
        group("horror", "horror,scary,suspense", "👻", "The Shining;Get Out;Psycho;Halloween;The Exorcist;Scream;The Thing;Hereditary;The Babadook;A Nightmare on Elm Street"),
        group("musicals", "musical,music,dance", "🎭", "Singin' in the Rain;The Sound of Music;West Side Story;La La Land;Cabaret;Chicago;Moulin Rouge!;The Umbrellas of Cherbourg;Dreamgirls;In the Heights"),
        group("war and history", "war,history,epic", "🎖️", "Schindler's List;Saving Private Ryan;Lawrence of Arabia;Oppenheimer;12 Years a Slave;Dunkirk;The Bridge on the River Kwai;All Quiet on the Western Front;The Last Emperor;The Battle of Algiers"),
        group("drama classics", "drama,classic,american", "🎞️", "Citizen Kane;Casablanca;12 Angry Men;One Flew Over the Cuckoo's Nest;To Kill a Mockingbird;It's a Wonderful Life;On the Waterfront;The Grapes of Wrath;A Streetcar Named Desire;All About Eve"),
        group("modern drama", "drama,modern,character", "🎬", "The Shawshank Redemption;There Will Be Blood;Moonlight;Parasite;Whiplash;Manchester by the Sea;Nomadland;The Social Network;Boyhood;The Florida Project"),
        group("family", "family,children,heartwarming", "🏡", "E.T. the Extra-Terrestrial;The Goonies;Paddington 2;Mary Poppins;The Iron Giant;The Muppet Movie;Matilda;The Parent Trap;Babe;A Little Princess"),
        group("sports", "sports,competition,inspiring", "🏆", "Rocky;Remember the Titans;Moneyball;Hoosiers;Creed;A League of Their Own;Rudy;Field of Dreams;Ford v Ferrari;Bend It Like Beckham"),
        group("westerns", "western,frontier,adventure", "🐎", "The Good, the Bad and the Ugly;Unforgiven;The Searchers;High Noon;Butch Cassidy and the Sundance Kid;True Grit;Once Upon a Time in the West;Stagecoach;The Assassination of Jesse James by the Coward Robert Ford;The Wild Bunch"),
        group("mystery", "mystery,puzzle,suspense", "🔎", "Knives Out;Rear Window;Vertigo;Chinatown;Gone Girl;Memento;Zodiac;The Girl with the Dragon Tattoo;Prisoners;The Third Man"),
        group("international", "international,world cinema,drama", "🌏", "Seven Samurai;Amélie;Cinema Paradiso;City of God;In the Mood for Love;A Separation;Bicycle Thieves;The Lives of Others;Roma;The 400 Blows"),
        group("documentary", "documentary,nonfiction,real life", "📹", "Won't You Be My Neighbor?;Free Solo;13th;Apollo 11;The Act of Killing;Hoop Dreams;Man on Wire;Summer of Soul;March of the Penguins;Jiro Dreams of Sushi"),
        group("cult favorites", "cult,offbeat,comedy", "🛸", "The Rocky Horror Picture Show;Donnie Darko;The Room;Office Space;Clue;This Is Spinal Tap;The Warriors;Repo Man;Dazed and Confused;Scott Pilgrim vs. the World"),
        group("contemporary favorites", "contemporary,acclaimed,drama", "🌟", "Everything Everywhere All at Once;Barbie;The Grand Budapest Hotel;Lady Bird;The Holdovers;Aftersun;Anatomy of a Fall;The Worst Person in the World;The Farewell;Minari"),
    ],
    "tv": [
        group("prestige drama", "drama,prestige,serial", "🏆", "Breaking Bad;The Sopranos;The Wire;Mad Men;Succession;Better Call Saul;The Crown;Six Feet Under;The Americans;Halt and Catch Fire"),
        group("comedies", "comedy,sitcom,ensemble", "😂", "The Office;Parks and Recreation;Friends;Seinfeld;30 Rock;Community;Abbott Elementary;Brooklyn Nine-Nine;Schitt's Creek;The Good Place"),
        group("science fiction", "science fiction,future,serial", "🚀", "Stranger Things;Black Mirror;Severance;The Expanse;Battlestar Galactica;Doctor Who;Star Trek: The Next Generation;Foundation;Silo;For All Mankind"),
        group("fantasy", "fantasy,magic,epic", "🐉", "Game of Thrones;House of the Dragon;The Last of Us;The Witcher;The Lord of the Rings: The Rings of Power;His Dark Materials;The Sandman;Shadow and Bone;Once Upon a Time;Merlin"),
        group("crime", "crime,detective,drama", "🕵️", "The Shield;Fargo;True Detective;Ozark;Mindhunter;Narcos;Boardwalk Empire;Peaky Blinders;The Night Of;Mare of Easttown"),
        group("mystery", "mystery,puzzle,suspense", "🔍", "Lost;Twin Peaks;Dark;Only Murders in the Building;Yellowjackets;The White Lotus;Broadchurch;Sherlock;Veronica Mars;Poker Face"),
        group("family animation", "animation,family,children", "🌈", "Bluey;Avatar: The Last Airbender;Gravity Falls;Adventure Time;Steven Universe;Phineas and Ferb;The Owl House;Hilda;SpongeBob SquarePants;Pokémon"),
        group("adult animation", "animation,adult,comedy", "📺", "The Simpsons;BoJack Horseman;Bob's Burgers;Futurama;South Park;Rick and Morty;King of the Hill;Archer;Family Guy;Invincible"),
        group("reality competition", "reality,competition,unscripted", "🥇", "Survivor;The Great British Bake Off;RuPaul's Drag Race;The Amazing Race;Top Chef;The Traitors;Project Runway;American Ninja Warrior;The Voice;Dancing with the Stars"),
        group("reality lifestyle", "reality,lifestyle,unscripted", "✨", "Queer Eye;Shark Tank;Love Is Blind;The Real Housewives of Beverly Hills;Selling Sunset;Fixer Upper;Tidying Up with Marie Kondo;Nailed It!;Chef's Table;Drive to Survive"),
        group("medical", "medical,workplace,drama", "🩺", "Grey's Anatomy;ER;House;The Pitt;Scrubs;Call the Midwife;The Resident;New Amsterdam;The Good Doctor;Nurse Jackie"),
        group("legal and political", "legal,political,workplace", "⚖️", "The West Wing;The Good Wife;The Good Fight;Suits;Scandal;How to Get Away with Murder;Boston Legal;Damages;Veep;The Diplomat"),
        group("historical", "history,period,drama", "🏛️", "Downton Abbey;Bridgerton;Shōgun;The Gilded Age;Vikings;Rome;The Queen's Gambit;Outlander;Pachinko;Deadwood"),
        group("action adventure", "action,adventure,thriller", "💥", "24;Reacher;Jack Ryan;The Mandalorian;Andor;Daredevil;The Boys;Alias;Warrior;Cobra Kai"),
        group("teen and coming of age", "teen,coming of age,drama", "🎒", "Gilmore Girls;Friday Night Lights;Freaks and Geeks;Never Have I Ever;Heartstopper;Derry Girls;Sex Education;Buffy the Vampire Slayer;One Tree Hill;My So-Called Life"),
        group("limited series", "limited series,drama,prestige", "🎞️", "Chernobyl;Band of Brothers;When They See Us;Maid;Unbelievable;Station Eleven;The Night Manager;Sharp Objects;Watchmen;Baby Reindeer"),
        group("classic tv", "classic,television,sitcom", "📼", "I Love Lucy;The Twilight Zone;M*A*S*H;The Mary Tyler Moore Show;Cheers;The Golden Girls;The Dick Van Dyke Show;Columbo;All in the Family;The Andy Griffith Show"),
        group("international", "international,subtitled,drama", "🌍", "Squid Game;Money Heist;Lupin;Kingdom;Extraordinary Attorney Woo;Gomorrah;Babylon Berlin;Call My Agent!;Borgen;Fauda"),
        group("food and travel", "food,travel,documentary", "🍜", "Anthony Bourdain: Parts Unknown;Somebody Feed Phil;Salt Fat Acid Heat;Ugly Delicious;No Reservations;Stanley Tucci: Searching for Italy;The Chef Show;Street Food;Taste the Nation;The Great British Menu"),
        group("nature and documentary", "documentary,nature,science", "🌿", "Planet Earth;Blue Planet II;Cosmos: A Spacetime Odyssey;Our Planet;The Last Dance;Ken Burns: The Civil War;Making a Murderer;The Vietnam War;Life;Prehistoric Planet"),
        group("modern favorites", "contemporary,acclaimed,character", "🌟", "Ted Lasso;Star Trek: Strange New Worlds;The Bear;Hacks;Reservation Dogs;Slow Horses;The Marvelous Mrs. Maisel;Fleabag;Atlanta;Beef"),
    ],
    "books": [
        group("literary classics", "classic,literary,novel", "📜", "Pride and Prejudice;To Kill a Mockingbird;The Great Gatsby;Jane Eyre;Wuthering Heights;Little Women;Anna Karenina;Middlemarch;The Grapes of Wrath;Beloved"),
        group("fantasy", "fantasy,magic,epic", "🐉", "The Lord of the Rings;Harry Potter series;A Song of Ice and Fire;The Chronicles of Narnia;The Name of the Wind;Mistborn;The Wheel of Time;Earthsea Cycle;The Broken Earth trilogy;The Priory of the Orange Tree"),
        group("science fiction", "science fiction,future,space", "🚀", "Dune;The Hitchhiker's Guide to the Galaxy;Foundation;Neuromancer;The Left Hand of Darkness;Ender's Game;Project Hail Mary;The Three-Body Problem;Snow Crash;Hyperion"),
        group("mystery", "mystery,detective,crime", "🔎", "And Then There Were None;The Girl with the Dragon Tattoo;Gone Girl;The Thursday Murder Club;The Big Sleep;In the Woods;The No. 1 Ladies' Detective Agency;The Silent Patient;The Cuckoo's Calling;The Seven Deaths of Evelyn Hardcastle"),
        group("thrillers", "thriller,suspense,crime", "😱", "The Da Vinci Code;The Bourne Identity;The Day of the Jackal;The Talented Mr. Ripley;The Firm;Shutter Island;The Woman in the Window;Before I Go to Sleep;The Guest List;I Am Pilgrim"),
        group("romance", "romance,relationship,fiction", "❤️", "The Notebook;Outlander;Beach Read;Red, White & Royal Blue;The Time Traveler's Wife;Me Before You;The Hating Game;Book Lovers;The Kiss Quotient;One Day"),
        group("historical fiction", "historical,fiction,period", "🏛️", "The Book Thief;All the Light We Cannot See;The Nightingale;Wolf Hall;Pachinko;Homegoing;The Pillars of the Earth;A Gentleman in Moscow;The Other Boleyn Girl;The Underground Railroad"),
        group("young adult", "young adult,coming of age,fiction", "🎒", "The Hunger Games;The Fault in Our Stars;The Outsiders;A Wrinkle in Time;The Hate U Give;Six of Crows;The Perks of Being a Wallflower;The Giver;Children of Blood and Bone;Aristotle and Dante Discover the Secrets of the Universe"),
        group("children", "children,family,classic", "🧸", "Charlotte's Web;The Little Prince;Anne of Green Gables;Alice's Adventures in Wonderland;The Secret Garden;Matilda;Where the Wild Things Are;The Wind in the Willows;The Velveteen Rabbit;The Tale of Peter Rabbit"),
        group("horror", "horror,gothic,supernatural", "👻", "The Shining;Dracula;Frankenstein;The Haunting of Hill House;It;The Exorcist;Mexican Gothic;House of Leaves;Something Wicked This Way Comes;The Only Good Indians"),
        group("memoir", "memoir,biography,personal", "🪞", "Becoming;Educated;The Glass Castle;Born a Crime;Crying in H Mart;When Breath Becomes Air;Just Kids;Wild;Know My Name;The Year of Magical Thinking"),
        group("history", "history,nonfiction,society", "🗺️", "Sapiens;The Warmth of Other Suns;Guns, Germs, and Steel;Team of Rivals;SPQR;The Devil in the White City;The Wright Brothers;Bury My Heart at Wounded Knee;A People's History of the United States;The Splendid and the Vile"),
        group("science and nature", "science,nature,nonfiction", "🔬", "A Brief History of Time;The Immortal Life of Henrietta Lacks;The Sixth Extinction;Cosmos;The Selfish Gene;Braiding Sweetgrass;Silent Spring;The Gene;Entangled Life;The Hidden Life of Trees"),
        group("psychology", "psychology,behavior,nonfiction", "🧠", "Thinking, Fast and Slow;Man's Search for Meaning;Quiet;Influence;The Body Keeps the Score;Flow;Maybe You Should Talk to Someone;The Righteous Mind;Stumbling on Happiness;Emotional Intelligence"),
        group("business", "business,leadership,nonfiction", "💼", "The Lean Startup;Good to Great;Shoe Dog;Zero to One;The Hard Thing About Hard Things;Leaders Eat Last;Creativity, Inc.;Measure What Matters;The Innovator's Dilemma;Start with Why"),
        group("personal growth", "personal growth,habits,nonfiction", "🌱", "Atomic Habits;The 7 Habits of Highly Effective People;Deep Work;The Power of Habit;Essentialism;Four Thousand Weeks;Digital Minimalism;The Artist's Way;Big Magic;The Gifts of Imperfection"),
        group("essays and humor", "essays,humor,nonfiction", "😄", "Me Talk Pretty One Day;Bad Feminist;A Supposedly Fun Thing I'll Never Do Again;Men Explain Things to Me;Wow, No Thank You.;Consider the Lobster;Trick Mirror;Calypso;We Are Never Meeting in Real Life.;How to Be Black"),
        group("poetry", "poetry,verse,literary", "🪶", "The Odyssey;Leaves of Grass;The Waste Land;The Complete Poems of Emily Dickinson;The Collected Poems of Langston Hughes;Devotions;Milk and Honey;Citizen: An American Lyric;Night Sky with Exit Wounds;The Sun and Her Flowers"),
        group("graphic novels", "graphic novel,comics,illustrated", "💬", "Maus;Watchmen;Persepolis;The Sandman;Fun Home;Saga;Blankets;March;Nimona;Monstress"),
        group("food and travel", "food,travel,nonfiction", "🍲", "Kitchen Confidential;Salt, Fat, Acid, Heat;The Omnivore's Dilemma;A Cook's Tour;In Defense of Food;The Art of Fermentation;The Food Lab;Under the Tuscan Sun;A Walk in the Woods;On the Road"),
        group("contemporary book club", "contemporary,book club,fiction", "📚", "Circe;Parable of the Sower;All Systems Red;The Night Circus;The Secret History;Tomorrow, and Tomorrow, and Tomorrow;Lessons in Chemistry;Demon Copperhead;A Man Called Ove;The Seven Husbands of Evelyn Hugo"),
    ],
    "games": [
        group("open world", "video game,open world,adventure", "🗺️", "The Legend of Zelda: Breath of the Wild;Red Dead Redemption 2;Elden Ring;Grand Theft Auto V;The Witcher 3: Wild Hunt;The Elder Scrolls V: Skyrim;Ghost of Tsushima;Horizon Zero Dawn;Cyberpunk 2077;Assassin's Creed Odyssey"),
        group("platformers", "video game,platformer,action", "🍄", "Super Mario Odyssey;Super Mario Bros. Wonder;Celeste;Hollow Knight;Ori and the Blind Forest;Sonic Mania;Donkey Kong Country;Crash Bandicoot N. Sane Trilogy;Rayman Legends;Shovel Knight"),
        group("role playing", "video game,rpg,story", "⚔️", "Baldur's Gate 3;Final Fantasy VII;Persona 5 Royal;Mass Effect 2;Chrono Trigger;Dragon Quest XI;Disco Elysium;Divinity: Original Sin 2;Xenoblade Chronicles;Clair Obscur: Expedition 33"),
        group("action", "video game,action,combat", "💥", "God of War;Devil May Cry 5;Bayonetta 2;Sekiro: Shadows Die Twice;Nier: Automata;Control;Hi-Fi Rush;Metal Gear Solid V;Armored Core VI;Returnal"),
        group("shooters", "video game,shooter,multiplayer", "🎯", "Halo 3;DOOM Eternal;Half-Life 2;Titanfall 2;Call of Duty: Modern Warfare;Destiny 2;Apex Legends;Overwatch 2;Counter-Strike 2;Valorant"),
        group("cozy games", "video game,cozy,life simulation", "🌿", "Stardew Valley;Animal Crossing: New Horizons;The Sims 4;Disney Dreamlight Valley;Cozy Grove;Spiritfarer;Unpacking;A Short Hike;Ooblets;Coffee Talk"),
        group("strategy", "video game,strategy,tactics", "♟️", "Civilization VI;StarCraft II;XCOM 2;Age of Empires II;Total War: Warhammer III;Into the Breach;Fire Emblem: Three Houses;Crusader Kings III;Advance Wars;Command & Conquer: Red Alert 2"),
        group("simulation", "video game,simulation,management", "🏗️", "Cities: Skylines;Microsoft Flight Simulator;Euro Truck Simulator 2;Planet Coaster;RollerCoaster Tycoon 2;Two Point Hospital;Farming Simulator 25;PowerWash Simulator;Factorio;Kerbal Space Program"),
        group("puzzle", "video game,puzzle,logic", "🧩", "Portal 2;Tetris Effect: Connected;The Witness;Baba Is You;Return of the Obra Dinn;Outer Wilds;The Talos Principle;Professor Layton and the Curious Village;Monument Valley;Cocoon"),
        group("horror games", "video game,horror,survival", "👻", "Resident Evil 4;Silent Hill 2;Dead Space;Alien: Isolation;Amnesia: The Dark Descent;SOMA;Alan Wake 2;Until Dawn;Outlast;Phasmophobia"),
        group("sports games", "video game,sports,competition", "🏆", "EA Sports FC 25;NBA 2K25;MLB The Show 25;Madden NFL 25;NHL 25;Tony Hawk's Pro Skater 1 + 2;Rocket League;Wii Sports;PGA Tour 2K25;F1 25"),
        group("racing", "video game,racing,driving", "🏎️", "Mario Kart 8 Deluxe;Forza Horizon 5;Gran Turismo 7;Need for Speed: Most Wanted;Burnout Paradise;Dirt Rally 2.0;F-Zero GX;Trackmania;Assetto Corsa;Wreckfest"),
        group("fighting", "video game,fighting,competitive", "🥊", "Super Smash Bros. Ultimate;Street Fighter 6;Tekken 8;Mortal Kombat 1;Guilty Gear Strive;Dragon Ball FighterZ;Marvel vs. Capcom 2;Soulcalibur II;MultiVersus;Killer Instinct"),
        group("party games", "video game,party,local multiplayer", "🎉", "Mario Party Superstars;Jackbox Party Pack;Overcooked! 2;Fall Guys;Among Us;Gang Beasts;Moving Out 2;Ultimate Chicken Horse;Keep Talking and Nobody Explodes;Mario & Sonic at the Olympic Games"),
        group("sandbox", "video game,sandbox,creative", "🧱", "Minecraft;Roblox;Terraria;Fortnite;Garry's Mod;No Man's Sky;Dreams;Lego Worlds;Core Keeper;Starbound"),
        group("indie", "video game,indie,creative", "💡", "Hades;Undertale;Cuphead;Dead Cells;Slay the Spire;Vampire Survivors;Balatro;Dave the Diver;Dredge;Inscryption"),
        group("board strategy", "board game,strategy,tabletop", "🎲", "Catan;Ticket to Ride;Carcassonne;Pandemic;7 Wonders;Wingspan;Terraforming Mars;Azul;Splendor;Scythe"),
        group("party board games", "board game,party,social", "🥳", "Codenames;Telestrations;Dixit;Just One;Wavelength;Apples to Apples;Taboo;Monikers;The Resistance;Sushi Go Party!"),
        group("classic tabletop", "board game,classic,family", "♟️", "Chess;Scrabble;Monopoly;Clue;Risk;Backgammon;Checkers;Yahtzee;The Game of Life;Battleship"),
        group("card and tabletop roleplay", "card game,tabletop,rpg", "🃏", "Dungeons & Dragons;Magic: The Gathering;Pokémon Trading Card Game;Yu-Gi-Oh! Trading Card Game;Uno;Exploding Kittens;Dominion;Netrunner;Marvel Champions;Arkham Horror: The Card Game"),
        group("enduring video game favorites", "video game,enduring,series", "🕹️", "Tetris;Animal Crossing;Mario Kart;The Last of Us;It Takes Two;Pokémon Red and Blue;League of Legends;World of Warcraft;Uncharted 4: A Thief's End;Helldivers 2"),
    ],
    "activities": [
        group("walking and trails", "outdoors,walking,accessible", "🥾", "Neighborhood walking;Nature trail walking;Day hiking;Urban walking tour;Dog walking;Geocaching;Birdwatching walk;Forest bathing;Beach walking;Waterfall hike"),
        group("running", "outdoors,running,fitness", "🏃", "Road running;Trail running;Jogging;Parkrun;5K racing;10K racing;Half-marathon training;Marathon training;Sprint intervals;Orienteering"),
        group("cycling", "outdoors,cycling,fitness", "🚲", "Road cycling;Mountain biking;Gravel biking;Bike touring;BMX riding;Indoor cycling;E-bike exploring;Bikepacking;Track cycling;Cyclocross"),
        group("water paddling", "outdoors,water,paddling", "🛶", "Kayaking;Canoeing;Stand-up paddleboarding;Whitewater rafting;Sea kayaking;Dragon boating;Rowing;Packrafting;Outrigger canoeing;Pedal boating"),
        group("water and beach", "outdoors,water,beach", "🏖️", "Swimming;Surfing;Snorkeling;Scuba diving;Bodyboarding;Windsurfing;Kitesurfing;Sailing;Wakeboarding;Beach volleyball"),
        group("winter sports", "outdoors,winter,snow", "⛷️", "Downhill skiing;Snowboarding;Cross-country skiing;Snowshoeing;Ice skating;Sledding;Curling;Ice hockey;Winter hiking;Fat-tire biking"),
        group("climbing", "outdoors,climbing,adventure", "🧗", "Indoor rock climbing;Bouldering;Outdoor rock climbing;Sport climbing;Trad climbing;Via ferrata;Rappelling;Mountaineering;Ice climbing;Tree climbing"),
        group("camping", "outdoors,camping,nature", "🏕️", "Tent camping;Car camping;Backpacking;RV camping;Cabin camping;Hammock camping;Glamping;Backyard camping;Overlanding;Wilderness survival practice"),
        group("team sports", "sports,team,ball", "⚽", "Soccer;Basketball;Baseball;Softball;Volleyball;Flag football;Rugby;Ultimate frisbee;Field hockey;Kickball"),
        group("racket and target", "sports,racket,target", "🎾", "Tennis;Pickleball;Badminton;Table tennis;Squash;Racquetball;Archery;Darts;Disc golf;Bowling"),
        group("fitness", "fitness,gym,strength", "🏋️", "Weightlifting;Bodyweight training;CrossFit;Circuit training;Kettlebell training;Calisthenics;Resistance-band workouts;Rowing machine workouts;Stair climbing;Trampoline fitness"),
        group("mindful movement", "fitness,mindful,low impact", "🧘", "Yoga;Pilates;Tai chi;Qigong;Barre;Stretching;Breathwork;Walking meditation;Chair yoga;Aqua aerobics"),
        group("dance", "dance,music,social", "💃", "Salsa dancing;Ballroom dancing;Hip-hop dance;Ballet;Tap dancing;Swing dancing;Line dancing;Contemporary dance;Zumba;Contra dancing"),
        group("visual arts", "creative,art,making", "🎨", "Watercolor painting;Oil painting;Acrylic painting;Sketching;Digital illustration;Photography;Printmaking;Collage;Calligraphy;Urban sketching"),
        group("crafts", "creative,craft,making", "🧶", "Knitting;Crocheting;Sewing;Quilting;Embroidery;Needle felting;Jewelry making;Candle making;Soap making;Leathercraft"),
        group("clay and wood", "creative,workshop,making", "🏺", "Wheel pottery;Hand-building pottery;Woodworking;Wood carving;Furniture refinishing;Glassblowing;Stained glass;Metalworking;Blacksmithing;Stone carving"),
        group("music making", "creative,music,performance", "🎵", "Playing guitar;Playing piano;Singing;Playing drums;Playing violin;Songwriting;Choir singing;Music production;DJing;Ukulele playing"),
        group("food making", "food,cooking,learning", "🍳", "Home cooking;Baking bread;Cake decorating;Grilling;Smoking barbecue;Making pasta;Fermentation;Cocktail making;Coffee brewing;Cheese making"),
        group("games and puzzles", "indoors,games,social", "🧩", "Board game night;Video gaming;Jigsaw puzzles;Crossword puzzles;Sudoku;Trivia night;Escape rooms;Chess club;Role-playing games;Karaoke"),
        group("community and learning", "social,learning,community", "🤝", "Book club;Volunteering;Community gardening;Language learning;Art gallery visits;Live theater;Concertgoing;Genealogy;Stargazing;Creative writing"),
        group("social leisure", "social,leisure,outings", "🎉", "Hiking;Beach Day;Cooking with Friends;Roller Skating;Reading in the Park;Bike Ride;Gardening;Live Comedy;Picnic;Visiting a Museum"),
    ],
    "foods": [
        group("burgers and sandwiches", "american,sandwich,savory", "🍔", "Cheeseburger;Bacon cheeseburger;Fried chicken sandwich;Grilled cheese;Philly cheesesteak;Reuben sandwich;Pulled pork sandwich;Lobster roll;French dip;Cuban sandwich"),
        group("pizza and flatbreads", "italian,bread,cheese", "🍕", "Margherita pizza;Pepperoni pizza;New York-style pizza;Chicago deep-dish pizza;Neapolitan pizza;Detroit-style pizza;Sicilian pizza;White pizza;Calzone;Focaccia"),
        group("mexican", "mexican,latin,savory", "🌮", "Tacos al pastor;Carne asada tacos;Birria tacos;Chicken enchiladas;Chile relleno;Tamales;Pozole;Mole poblano;Chilaquiles;Elote"),
        group("italian pasta", "italian,pasta,savory", "🍝", "Spaghetti carbonara;Lasagna;Fettuccine Alfredo;Cacio e pepe;Penne arrabbiata;Pesto pasta;Ravioli;Gnocchi;Bolognese;Baked ziti"),
        group("chinese", "chinese,asian,savory", "🥡", "Peking duck;Xiaolongbao;Mapo tofu;Kung pao chicken;Char siu;Beef chow fun;Hot pot;Scallion pancakes;Dan dan noodles;Congee"),
        group("japanese", "japanese,asian,umami", "🍣", "Sushi;Sashimi;Tonkotsu ramen;Tempura;Chicken katsu;Okonomiyaki;Takoyaki;Udon;Yakitori;Japanese curry"),
        group("korean", "korean,asian,spicy", "🥢", "Korean barbecue;Bibimbap;Kimchi;Tteokbokki;Japchae;Bulgogi;Korean fried chicken;Kimchi jjigae;Sundubu-jjigae;Naengmyeon"),
        group("thai and vietnamese", "southeast asian,herbs,noodles", "🍜", "Pad thai;Green curry;Tom yum soup;Pad see ew;Mango sticky rice;Pho;Bánh mì;Bún bò Huế;Fresh spring rolls;Bún chả"),
        group("indian", "indian,south asian,spiced", "🍛", "Butter chicken;Chicken tikka masala;Biryani;Palak paneer;Masala dosa;Chana masala;Samosas;Dal makhani;Vindaloo;Garlic naan"),
        group("middle eastern", "middle eastern,mediterranean,spiced", "🧆", "Hummus;Falafel;Chicken shawarma;Beef kebab;Shakshuka;Baba ganoush;Tabbouleh;Kibbeh;Manakish;Fattoush"),
        group("breakfast", "breakfast,brunch,morning", "🥞", "Pancakes;French toast;Eggs Benedict;Breakfast burrito;Biscuits and gravy;Avocado toast;Belgian waffles;Huevos rancheros;Corned beef hash;Shakshuka toast"),
        group("soups and stews", "soup,stew,comfort", "🥣", "Chicken noodle soup;Tomato soup;French onion soup;New England clam chowder;Beef stew;Chili con carne;Gumbo;Lentil soup;Minestrone;Matzo ball soup"),
        group("salads and bowls", "salad,bowl,fresh", "🥗", "Caesar salad;Greek salad;Cobb salad;Caprese salad;Niçoise salad;Poke bowl;Burrito bowl;Buddha bowl;Fattoush salad;Thai beef salad"),
        group("seafood", "seafood,fish,savory", "🦞", "Fish and chips;Grilled salmon;Crab cakes;Shrimp and grits;Lobster thermidor;Ceviche;Paella;Cioppino;Garlic butter shrimp;Blackened fish"),
        group("barbecue and comfort", "american,barbecue,comfort", "🍖", "Smoked brisket;Barbecue ribs;Pulled pork;Fried chicken;Chicken and waffles;Macaroni and cheese;Meatloaf;Chicken pot pie;Mashed potatoes;Cornbread"),
        group("african and caribbean", "african,caribbean,spiced", "🌍", "Jollof rice;Injera with doro wat;Suya;Egusi soup;Bunny chow;Jerk chicken;Curry goat;Ackee and saltfish;Ropa vieja;Mofongo"),
        group("breads and pastries", "bakery,bread,pastry", "🥐", "Croissant;Sourdough bread;Baguette;Cinnamon roll;Chocolate chip muffin;Everything bagel;Brioche;Banana bread;Pretzel;Buttermilk biscuit"),
        group("cakes and pies", "dessert,baked,sweet", "🍰", "Chocolate cake;Cheesecake;Carrot cake;Red velvet cake;Tiramisu;Apple pie;Key lime pie;Pecan pie;Lemon meringue pie;Tres leches cake"),
        group("frozen and creamy desserts", "dessert,frozen,sweet", "🍨", "Vanilla ice cream;Chocolate ice cream;Strawberry ice cream;Gelato;Frozen yogurt;Mochi ice cream;Kulfi;Sorbet;Milkshake;Banana split"),
        group("cookies and sweets", "dessert,sweet,snack", "🍪", "Chocolate chip cookies;Brownies;Donuts;Macarons;Baklava;Churros;Crème brûlée;Cannoli;Rice pudding;Flan"),
        group("everyday favorites and produce", "everyday,produce,comfort", "🍓", "Crispy Fries;Strawberries;Spicy Ramen;Fresh Mango;Street Tacos;Ice Cream;Fresh Bread;Watermelon;Honeycrisp Apple;Ripe Peaches"),
    ],
}


ALIASES = {
    ("restaurants", "In-N-Out"): ["in n out", "in and out", "innout", "in-n-out burger"],
    ("restaurants", "McDonald's"): ["mcdonalds", "mickey d's"],
    ("restaurants", "Chick-fil-A"): ["chick fil a", "chickfila"],
    ("restaurants", "QDOBA"): ["qdoba mexican eats"],
    ("restaurants", "Fogo de Chão"): ["fogo de chao"],
    ("music", "Beyoncé"): ["beyonce"],
    ("music", "Tiësto"): ["tiesto"],
    ("movies", "E.T. the Extra-Terrestrial"): ["et", "e t"],
    ("movies", "Star Wars: A New Hope"): ["star wars", "episode iv", "a new hope"],
    ("tv", "M*A*S*H"): ["mash"],
    ("tv", "Shōgun"): ["shogun"],
    ("books", "Harry Potter series"): ["harry potter"],
    ("books", "The Lord of the Rings"): ["lord of the rings", "lotr"],
    ("games", "EA Sports FC 25"): ["fifa", "fc 25"],
    ("games", "Pokémon Trading Card Game"): ["pokemon tcg", "pokemon cards"],
    ("foods", "Bánh mì"): ["banh mi"],
    ("foods", "Bún bò Huế"): ["bun bo hue"],
    ("foods", "Xiaolongbao"): ["soup dumplings"],
    ("foods", "Crème brûlée"): ["creme brulee"],
}

# Search vocabulary is user-facing compatibility data. These aliases shipped in
# version 0.1 and must survive when a retained item gains richer catalog data.
LEGACY_ALIASES_BY_ID = {
    "restaurant_in_n_out": ["in n out", "in and out", "innout"],
    "restaurant_din_tai_fung": ["dtf", "dumplings"],
    "restaurant_chipotle": ["chipotle mexican grill", "burritos"],
    "restaurant_shake_shack": ["shakeshack"],
    "music_beyonce": ["beyonce", "queen b"],
    "music_daft_punk": ["electronic duo"],
    "music_fleetwood_mac": ["rumours"],
    "music_kendrick_lamar": ["kendrick"],
    "music_taylor_swift": ["t swift", "swift"],
    "music_bad_bunny": ["benito"],
    "movie_spirited_away": ["studio ghibli", "ghibli"],
    "movie_the_matrix": ["matrix"],
    "movie_moonlight": ["moon light"],
    "movie_parasite": ["gisaengchung"],
    "movie_princess_bride": ["princess bride"],
    "movie_everything_everywhere": ["eeaao", "everything everywhere"],
    "movie_into_spider_verse": ["spiderverse", "spider verse"],
    "movie_arrival": ["arrival movie"],
    "movie_knives_out": ["knivesout"],
    "movie_mad_max_fury_road": ["fury road", "mad max"],
    "movie_paddington_2": ["paddington"],
    "movie_get_out": ["getout"],
    "tv_the_bear": ["bear"],
    "tv_severance": ["lumon"],
    "tv_ted_lasso": ["lasso"],
    "tv_abbott_elementary": ["abbott"],
    "tv_bluey": ["bluey show"],
    "tv_schitts_creek": ["schitts creek", "schitt creek"],
    "tv_only_murders": ["only murders", "omitb"],
    "tv_avatar_airbender": ["atla", "last airbender"],
    "tv_pachinko": ["pachinko series"],
    "tv_great_british_bake_off": ["gbbo", "baking show"],
    "tv_poker_face": ["pokerface"],
    "tv_star_trek_strange_new_worlds": ["strange new worlds", "star trek"],
    "book_parable_sower": ["octavia butler", "parable"],
    "book_project_hail_mary": ["hail mary", "andy weir"],
    "book_pride_prejudice": ["jane austen", "pride prejudice"],
    "book_murderbot": ["murderbot", "martha wells"],
    "book_educated": ["tara westover"],
    "book_left_hand_darkness": ["ursula le guin", "left hand of darkness"],
    "book_circe": ["madeline miller"],
    "book_braiding_sweetgrass": ["robin wall kimmerer", "sweetgrass"],
    "book_hitchhikers_guide": ["hitchhikers guide", "douglas adams"],
    "book_night_circus": ["night circus", "erin morgenstern"],
    "book_secret_history": ["donna tartt"],
    "book_pachinko": ["min jin lee", "pachinko novel"],
    "game_stardew_valley": ["stardew"],
    "game_hades": ["hades game"],
    "game_zelda_botw": ["zelda", "botw", "breath of the wild"],
    "game_mario_kart": ["mariokart"],
    "game_wingspan": ["wingspan board game"],
    "game_codenames": ["code names", "party game"],
    "game_celeste": ["celeste game"],
    "game_baldurs_gate_3": ["bg3", "baldurs gate"],
    "game_tetris": ["tetromino"],
    "game_animal_crossing": ["acnh", "new horizons"],
    "game_chess": ["chess game"],
    "game_dungeons_dragons": ["dnd", "d&d", "tabletop rpg"],
    "activity_hiking": ["hike", "trail walking"],
    "activity_beach_day": ["beach", "ocean"],
    "activity_museum": ["museum", "art museum"],
    "activity_cooking_friends": ["cook together", "dinner party"],
    "activity_roller_skating": ["rollerskating", "skating"],
    "activity_karaoke": ["singing"],
    "activity_reading_park": ["park reading", "read outside"],
    "activity_board_game_night": ["game night", "board games"],
    "activity_bike_ride": ["cycling", "biking"],
    "activity_gardening": ["garden", "plants"],
    "activity_live_comedy": ["comedy show", "stand up"],
    "activity_picnic": ["picnic outside"],
    "food_crispy_fries": ["french fries", "fries"],
    "food_strawberries": ["strawberry"],
    "food_dumplings": ["xiaolongbao", "xiao long bao", "dumplings"],
    "food_chocolate_cake": ["cake", "chocolate"],
    "food_spicy_ramen": ["ramen", "noodle soup"],
    "food_mango": ["mango"],
    "food_grilled_cheese": ["cheese toastie"],
    "food_sushi": ["nigiri", "maki"],
    "food_avocado_toast": ["avo toast"],
    "food_tacos": ["tacos", "taco"],
    "food_ice_cream": ["gelato", "icecream"],
    "food_fresh_bread": ["bread", "bakery bread"],
}

# The app supports API 23 and does not bundle an emoji font. Limit generated
# tiles to symbols that already shipped in the original MVP on that baseline.
API_23_SAFE_EMOJI = frozenset({
    "◼️", "☕", "♟️", "♠️", "⚔️", "⚽", "⛰️", "✏️", "✨", "❄️",
    "🌊", "🌌", "🌙", "🌪️", "🌱", "🌳", "🌸", "🌻", "🌾", "🌿",
    "🍎", "🍓", "🍔", "🍕", "🍖", "🍛", "🍜", "🍞", "🍟", "🍣",
    "🍨", "🍰", "🍳", "🍴", "🍽️", "🎙️", "🎤", "🎧", "🎪", "🎭",
    "🎲", "🎵", "🎷", "🎸", "🎻", "🎮", "🏎️", "🏔️", "🏖️", "🏛️",
    "🏝️", "🏨", "🏺", "🐉", "🐦", "🐰", "🐶", "🐻", "👟", "👾",
    "💊", "💌", "💿", "📖", "📚", "📺", "🔍", "🔥", "🕵️", "🕶️",
    "🕷️", "🖖", "🖥️", "🗡️", "🚀", "🚲",
})

SAFE_EMOJI_FALLBACK = {
    "restaurants": "🍴",
    "music": "🎵",
    "movies": "🎭",
    "tv": "📺",
    "books": "📖",
    "games": "🎲",
    "activities": "👟",
    "foods": "🍴",
}


PALETTES = ["RUBY", "EMERALD", "AMETHYST", "SAPPHIRE", "TOPAZ", "AQUAMARINE", "CITRINE", "GARNET"]

CATEGORY_PREFIX = {
    "restaurants": "restaurant",
    "music": "music",
    "movies": "movie",
    "tv": "tv",
    "books": "book",
    "games": "game",
    "activities": "activity",
    "foods": "food",
}

# These IDs shipped in the original MVP. Preserve them so an in-place app update
# keeps real saved favorites rather than silently orphaning user-owned data.
ID_OVERRIDES = {
    ("movies", "The Princess Bride"): "movie_princess_bride",
    ("movies", "Spider-Man: Into the Spider-Verse"): "movie_into_spider_verse",
    ("movies", "Everything Everywhere All at Once"): "movie_everything_everywhere",
    ("tv", "Schitt's Creek"): "tv_schitts_creek",
    ("tv", "Only Murders in the Building"): "tv_only_murders",
    ("tv", "Avatar: The Last Airbender"): "tv_avatar_airbender",
    ("tv", "The Great British Bake Off"): "tv_great_british_bake_off",
    ("books", "Pride and Prejudice"): "book_pride_prejudice",
    ("books", "The Left Hand of Darkness"): "book_left_hand_darkness",
    ("books", "The Hitchhiker's Guide to the Galaxy"): "book_hitchhikers_guide",
    ("books", "Parable of the Sower"): "book_parable_sower",
    ("books", "All Systems Red"): "book_murderbot",
    ("books", "The Night Circus"): "book_night_circus",
    ("books", "The Secret History"): "book_secret_history",
    ("games", "The Legend of Zelda: Breath of the Wild"): "game_zelda_botw",
    ("games", "Baldur's Gate 3"): "game_baldurs_gate_3",
    ("foods", "Xiaolongbao"): "food_dumplings",
    ("activities", "Cooking with Friends"): "activity_cooking_friends",
    ("activities", "Reading in the Park"): "activity_reading_park",
    ("activities", "Visiting a Museum"): "activity_museum",
    ("foods", "Fresh Mango"): "food_mango",
    ("foods", "Street Tacos"): "food_tacos",
}


LEGACY_VISUALS = {
    'restaurant_in_n_out': ('🍔', 'RUBY'),
    'restaurant_din_tai_fung': ('🍜', 'GARNET'),
    'restaurant_chipotle': ('🍴', 'EMERALD'),
    'restaurant_shake_shack': ('🍔', 'EMERALD'),
    'music_beyonce': ('🎤', 'CITRINE'),
    'music_daft_punk': ('👾', 'AQUAMARINE'),
    'music_fleetwood_mac': ('🌙', 'AMETHYST'),
    'music_kendrick_lamar': ('🎙️', 'RUBY'),
    'music_taylor_swift': ('✨', 'TOPAZ'),
    'music_bad_bunny': ('🐰', 'GARNET'),
    'movie_spirited_away': ('🐉', 'EMERALD'),
    'movie_the_matrix': ('💊', 'EMERALD'),
    'movie_moonlight': ('🌊', 'SAPPHIRE'),
    'movie_parasite': ('⛰️', 'GARNET'),
    'movie_princess_bride': ('⚔️', 'RUBY'),
    'movie_everything_everywhere': ('🍞', 'AMETHYST'),
    'movie_into_spider_verse': ('🕷️', 'RUBY'),
    'movie_arrival': ('🚀', 'AQUAMARINE'),
    'movie_knives_out': ('🔍', 'TOPAZ'),
    'movie_mad_max_fury_road': ('🔥', 'CITRINE'),
    'movie_paddington_2': ('🐻', 'RUBY'),
    'movie_get_out': ('☕', 'GARNET'),
    'tv_the_bear': ('🐻', 'SAPPHIRE'),
    'tv_severance': ('🖥️', 'AQUAMARINE'),
    'tv_ted_lasso': ('⚽', 'SAPPHIRE'),
    'tv_abbott_elementary': ('✏️', 'CITRINE'),
    'tv_bluey': ('🐶', 'SAPPHIRE'),
    'tv_schitts_creek': ('🏨', 'AMETHYST'),
    'tv_only_murders': ('🕵️', 'RUBY'),
    'tv_avatar_airbender': ('🌪️', 'AQUAMARINE'),
    'tv_pachinko': ('🌸', 'RUBY'),
    'tv_great_british_bake_off': ('🍰', 'TOPAZ'),
    'tv_poker_face': ('♠️', 'GARNET'),
    'tv_star_trek_strange_new_worlds': ('🖖', 'SAPPHIRE'),
    'book_parable_sower': ('🌱', 'EMERALD'),
    'book_project_hail_mary': ('🚀', 'AQUAMARINE'),
    'book_pride_prejudice': ('💌', 'RUBY'),
    'book_murderbot': ('👾', 'SAPPHIRE'),
    'book_educated': ('🏔️', 'TOPAZ'),
    'book_left_hand_darkness': ('❄️', 'AQUAMARINE'),
    'book_circe': ('🏺', 'CITRINE'),
    'book_braiding_sweetgrass': ('🌾', 'EMERALD'),
    'book_hitchhikers_guide': ('🌌', 'SAPPHIRE'),
    'book_night_circus': ('🎪', 'AMETHYST'),
    'book_secret_history': ('🏛️', 'GARNET'),
    'book_pachinko': ('📖', 'RUBY'),
    'game_stardew_valley': ('🌻', 'EMERALD'),
    'game_hades': ('🔥', 'RUBY'),
    'game_zelda_botw': ('🗡️', 'EMERALD'),
    'game_mario_kart': ('🏎️', 'RUBY'),
    'game_wingspan': ('🐦', 'AQUAMARINE'),
    'game_codenames': ('🕶️', 'SAPPHIRE'),
    'game_celeste': ('🍓', 'AMETHYST'),
    'game_baldurs_gate_3': ('🐉', 'GARNET'),
    'game_tetris': ('◼️', 'AQUAMARINE'),
    'game_animal_crossing': ('🏝️', 'EMERALD'),
    'game_chess': ('♟️', 'TOPAZ'),
    'game_dungeons_dragons': ('🎲', 'AMETHYST'),
    'activity_hiking': ('👟', 'EMERALD'),
    'activity_beach_day': ('🏖️', 'AQUAMARINE'),
    'activity_museum': ('🏛️', 'TOPAZ'),
    'activity_cooking_friends': ('🍳', 'RUBY'),
    'activity_roller_skating': ('🚲', 'AMETHYST'),
    'activity_karaoke': ('🎤', 'RUBY'),
    'activity_reading_park': ('🌳', 'EMERALD'),
    'activity_board_game_night': ('🎲', 'SAPPHIRE'),
    'activity_bike_ride': ('🚲', 'AQUAMARINE'),
    'activity_gardening': ('🌱', 'EMERALD'),
    'activity_live_comedy': ('🎭', 'CITRINE'),
    'activity_picnic': ('🍎', 'RUBY'),
    'food_crispy_fries': ('🍟', 'CITRINE'),
    'food_strawberries': ('🍓', 'RUBY'),
    'food_dumplings': ('🍜', 'TOPAZ'),
    'food_chocolate_cake': ('🍰', 'GARNET'),
    'food_spicy_ramen': ('🍜', 'RUBY'),
    'food_mango': ('🍎', 'CITRINE'),
    'food_grilled_cheese': ('🍞', 'TOPAZ'),
    'food_sushi': ('🍣', 'SAPPHIRE'),
    'food_avocado_toast': ('🌿', 'EMERALD'),
    'food_tacos': ('🍴', 'CITRINE'),
    'food_ice_cream': ('🍨', 'AMETHYST'),
    'food_fresh_bread': ('🍞', 'TOPAZ'),
}

def slug(value: str) -> str:
    plain = unicodedata.normalize("NFKD", value).encode("ascii", "ignore").decode().lower()
    return re.sub(r"[^a-z0-9]+", "_", plain).strip("_")


def build() -> list[dict[str, object]]:
    records: list[dict[str, object]] = []
    for category, groups in CATEGORIES.items():
        if len(groups) != 21:
            raise ValueError(f"{category}: expected 21 facets, got {len(groups)}")
        seen_names: set[str] = set()
        seen_ids: set[str] = set()
        for facet, tags, _, names in groups:
            if len(names) != 10:
                raise ValueError(f"{category}/{facet}: expected 10 items, got {len(names)}")
            for name in names:
                folded = name.casefold()
                if folded in seen_names:
                    raise ValueError(f"{category}: duplicate name {name}")
                seen_names.add(folded)

        # Round-robin facets so rank 1..20 is a diverse set of recognizable heads,
        # then each facet progresses from broader to more specialist choices.
        for depth in range(10):
            for facet_index, (facet, tags, emoji, names) in enumerate(groups):
                name = names[depth]
                item_id = ID_OVERRIDES.get(
                    (category, name),
                    f"{CATEGORY_PREFIX[category]}_{slug(name)}",
                )
                if item_id in seen_ids:
                    raise ValueError(f"{category}: duplicate id {item_id}")
                seen_ids.add(item_id)
                popularity = depth * len(groups) + facet_index + 1
                legacy_visual = LEGACY_VISUALS.get(item_id)
                candidate_emoji = legacy_visual[0] if legacy_visual else emoji
                tile_emoji = (
                    candidate_emoji
                    if candidate_emoji in API_23_SAFE_EMOJI
                    else SAFE_EMOJI_FALLBACK[category]
                )
                aliases = list(dict.fromkeys([
                    *LEGACY_ALIASES_BY_ID.get(item_id, []),
                    *ALIASES.get((category, name), []),
                ]))
                records.append(
                    {
                        "id": item_id,
                        "name": name,
                        "category": category,
                        "emoji": tile_emoji,
                        "palette": legacy_visual[1] if legacy_visual else PALETTES[
                            (facet_index + depth) % len(PALETTES)
                        ],
                        "aliases": aliases,
                        "facet": facet,
                        "tags": list(dict.fromkeys([facet, *tags])),
                        "popularity": popularity,
                    }
                )
    generated_ids = {record["id"] for record in records}
    missing_legacy_ids = set(LEGACY_VISUALS) - generated_ids
    if missing_legacy_ids:
        raise ValueError(f"missing shipped favorite IDs: {sorted(missing_legacy_ids)}")
    missing_alias_ids = set(LEGACY_ALIASES_BY_ID) - generated_ids
    if missing_alias_ids:
        raise ValueError(f"missing shipped alias IDs: {sorted(missing_alias_ids)}")
    unsafe_emoji = {record["emoji"] for record in records} - API_23_SAFE_EMOJI
    if unsafe_emoji:
        raise ValueError(f"API 23-unsafe catalog emoji: {sorted(unsafe_emoji)}")
    return records


def main() -> None:
    records = build()
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    lines = [json.dumps(record, ensure_ascii=False, separators=(",", ":")) for record in records]
    OUTPUT.write_text("[\n" + ",\n".join(lines) + "\n]\n", encoding="utf-8")
    counts = {category: sum(item["category"] == category for item in records) for category in CATEGORIES}
    print(f"Wrote {len(records)} items to {OUTPUT.relative_to(ROOT)}: {counts}")


if __name__ == "__main__":
    main()
