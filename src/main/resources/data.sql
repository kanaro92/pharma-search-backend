-- Add test pharmacies in Paris region
INSERT INTO pharmacies (name, address, latitude, longitude, phone_number) VALUES
('Pharmacie de la Tour Eiffel', '12 Avenue de la Bourdonnais, 75007 Paris', 48.8584, 2.2945, '+33145555555'),
('Pharmacie du Marais', '25 Rue des Archives, 75004 Paris', 48.8589, 2.3567, '+33144444444'),
('Pharmacie Saint-Germain', '144 Boulevard Saint-Germain, 75006 Paris', 48.8527, 2.3335, '+33143333333'),
('Pharmacie Montmartre', '56 Rue des Abbesses, 75018 Paris', 48.8845, 2.3370, '+33142222222'),
('Pharmacie Bastille', '5 Place de la Bastille, 75004 Paris', 48.8533, 2.3692, '+33141111111');

-- Add test pharmacies in Gif-sur-Yvette region
INSERT INTO pharmacies (name, address, latitude, longitude, phone_number) VALUES
('Pharmacie de Gif', '12 Route de Chartres, Gif-sur-Yvette', 48.6824, 2.1679, '+33169999999'),
('Pharmacie du Centre', '2 Place du Marché Neuf, Gif-sur-Yvette', 48.6850, 2.1340, '+33169888888'),
('Pharmacie de la Vallée', '15 Avenue du Général Leclerc, Gif-sur-Yvette', 48.6797, 2.1347, '+33169777777');
