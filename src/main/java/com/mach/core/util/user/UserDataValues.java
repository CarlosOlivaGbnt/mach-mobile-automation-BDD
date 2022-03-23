package com.mach.core.util.user;

public class UserDataValues {

    private static String[] firstNames = {
            "Jose", "Luis", "Juan", "Francisco", "Antonio", "Jesus", "Miguel", "Angel",
            "Pedro", "Alejandro", "Manuel", "Carlos", "Roberto", "Jorge", "Fernando",
            "Ricardo", "Javier", "Martin", "Rafael", "Raul", "Arturo", "Daniel",
            "Eduardo", "Enrique", "Mario", "Sergio", "Gerardo", "Salvador", "Marco",
            "Alfredo", "David", "Armando", "Alberto", "Oscar", "Ramon", "Guillermo",
            "Ruben", "Jaime", "Felipe", "Andres", "Pablo", "Gabriel", "Hector",
            "Alfonso", "Agustin", "Ignacio", "Victor", "Rogelio", "Gustavo", "Ernesto",
            "Rodolfo", "Gilberto", "Vicente", "Tomas", "Israel", "Cesar", "Adrian",
            "Ismael", "Santiago", "Humberto", "Gregorio", "Joel", "Esteban", "Nicolas",
            "Omar", "Moises", "Felix", "Lorenzo", "Samuel", "Ramiro", "Abel","Fredy",
            "Marcos", "Rodrigo", "Edgar", "Isidro", "Leonardo", "Benjamin", "Julio",
            "Raymundo", "Hugo", "Saul", "Benito", "Rigoberto", "Bob", "Saul","Johnny",
            "Chris", "Willian", "Charlie", "Nylah", "Valentin", "Bruno", "Alvaro", "Kris",
            "Guadalupe", "Maria", "Leena", "Elissa", "Bianca", "Tina", "Anya", "Aida", "Fiona",
            "Margarita", "Veronica", "Elena", "Josefina", "Leticia", "Teresa", "Patricia",
            "Rosa", "Martha", "Alicia", "Yolanda", "Francisca", "Silvia", "Elizabeth",
            "Gloria", "Ana", "Gabriela", "Alejandra", "Luisa", "Lourdes", "Adriana",
            "Araceli", "Antonia", "Lucia", "Carmen", "Irma", "Claudia", "Beatriz",
            "Isabel", "Laura", "Maribel", "Graciela", "Virginia", "Catalina", "Esperanza",
            "Angelica", "Maricela", "Cecilia", "Susana", "Cristina", "Julia", "Concepcion",
            "Victoria", "Ofelia", "Rocio", "Carolina", "Raquel", "Petra", "Lorena", "Reyna",
            "Sandra", "Paula", "Guillermina", "Sara", "Elvira", "Manuela", "Marisol",
            "Monica", "Erika", "Celia", "Luz", "Irene", "Magdalena", "Estela", "Angela",
            "Rosario", "Esther", "Eva", "Norma", "Aurora", "Socorro", "Consuelo", "Lidia",
            "Bertha", "Sofia", "Dolores", "Rosalba", "Liliana", "Andrea", "Adela", "Mariana",
            "Fabiola", "Karina", "Martina", "Marcela", "Miriam", "Mercedes", "Marina", "Amalia",
            "Olivia", "Angelina", "Sonia", "Agustina", "Edith", "Lilia", "Micaela", "Olga",
            "Leidy", "Erica", "Viviana", "Nadya", "Ariel", "Daniela", "Claudio", "Nelson",
            "Walter", "Wilmer", "Waldermar", "Washington", "Sandro", "Camilo", "Camila", "Damian",
            "Asucena", "Obdulio", "Celeste", "Celestina", "Sol", "Soledad", "Tatiana", "Gaston",
            "Fiorella", "Emeregildo", "Sebastian", "Selena", "Penelope", "Julie", "Kim", "Ruby"
    };

    private static String[] lastNames = {
            "Hernandez", "Garcia", "Martinez", "Lopez", "Gonzalez", "Rodriguez", "Perez", "Sanchez",
            "Ramirez", "Cruz", "Flores", "Gomez", "Morales", "Vazquez", "Reyes", "Jimenez", "Torres",
            "Diaz", "Gutierrez", "Mendoza", "Ruiz", "Aguilar", "Ortiz", "Castillo", "Moreno", "Romero",
            "Alvarez", "Chavez", "Rivera", "Juarez", "Ramos", "Mendez", "Dominguez", "Herrera", "Medina",
            "Vargas", "Castro", "Guzman", "Velazquez", "Munoz", "Rojas", "Contreras", "Salazar", "Luna",
            "DelaCruz", "Ortega", "Guerrero", "Santiago", "Estrada", "Bautista", "Cortes", "Soto",
            "Alvarado", "Espinoza", "Lara", "Avila", "Rios", "Cervantes", "Silva", "Delgado", "Vega",
            "Marquez", "Sandoval", "Fernandez", "Leon", "Carrillo", "Mejia", "Solis", "Nunez", "Rosas",
            "Valdez", "Ibarra", "Campos", "Santos", "Camacho", "Pena", "Maldonado", "Navarro", "Rosales",
            "Acosta", "Miranda", "Trejo", "Cabrera", "Valencia", "Nava", "Castaneda", "Pacheco",
            "Robles", "Molina", "Rangel", "Fuentes", "Huerta", "Meza", "Aguirre", "Cardenas", "Orozco",
            "Padilla", "Espinosa", "Ayala", "Salas", "Valenzuela", "Zuniga", "Ochoa", "Salinas",
            "Mora", "Tapia", "Serrano", "Duran", "Olvera", "Macias", "Zamora", "Calderon", "Arellano",
            "Suarez", "Barrera", "Zavala", "Villegas", "Gallegos", "Lozano", "Galvan", "Figueroa",
            "Beltran", "Franco", "Villanueva", "Sosa", "Montes", "Andrade", "Velasco", "Arias",
            "Marin", "Corona", "Garza", "Ponce", "Esquivel", "Pineda", "Alonso", "Palacios", "Antonio",
            "Vasquez", "Trujillo", "Cortez", "Rocha", "Rubio", "Bernal", "Benitez", "Escobar",
            "Villa", "Galindo", "Cuevas", "Bravo", "Cano", "Osorio", "Mata", "Carmona", "Montoya",
            "DeJesus", "Enriquez", "Cisneros", "Rivas", "Parra", "Resendiz", "Tellez", "Zarate",
            "Salgado", "DelaRosa", "Vera", "Tovar", "Arroyo", "Cordova", "Leyva", "Quintero",
            "Becerra", "Quiroz", "Barajas", "Avalos", "Peralta", "Roman", "Esparza", "Murillo",
            "Guevara", "Olivares", "Felix", "DeLeon", "Castellanos", "Villarreal", "Villalobos",
            "Lugo", "Angeles", "Montiel", "Segura", "Magana", "Saucedo", "Gallardo", "Mercado",
            "Navarrete", "Reyna", "Paredes", "Davila", "Leal", "Guerra", "Saldana", "Guillen",
            "Santana", "Uribe", "Monroy", "Pina", "Yanez", "Nieto", "Islas", "Granados", "Escobedo",
            "Zapata", "Caballero", "Solano", "Barron", "Zepeda", "Acevedo", "Arriaga",
            "Barrios", "Mondragon", "Galicia", "Godinez", "Ojeda", "Duarte", "Alfaro", "Medrano",
            "Rico", "Aguilera", "Gil", "Ventura", "Balderas", "Arredondo", "Coronado", "Escamilla",
            "Najera", "Palma", "Amador", "Blanco", "Ocampo", "Garduno", "Barragan", "Gamez", "Francisco",
            "Melendez", "Carbajal", "Hurtado", "Carrasco", "Bonilla", "Correa", "Sierra", "Anaya", "Carranza",
            "Romo", "Valdes", "Armenta", "Alcantara", "Escalante", "Arreola", "Quezada", "Alarcon", "Gaytan",
            "Renteria", "Vidal", "Baez", "DelosSantos", "Toledo", "Colin", "May", "Carrera", "Jaramillo",
            "Santillan", "Valle", "Varela", "Arenas", "Rendon", "Trevino", "Venegas", "Soriano", "Zaragoza",
            "Moran", "Aviles", "Aranda", "Lira", "Quintana", "Arteaga", "Valadez", "Cordero", "Sotelo",
            "DelaTorre", "Muniz", "Hidalgo", "Cazares", "Covarrubias", "Zamudio", "Ordonez", "Aparicio",
            "Baltazar", "Galvez", "Madrigal", "Techera", "Camejo", "Mujica", "Lacalle", "Sanguinetti", "Batlle",
            "Menem", "Maciel", "Carambula", "Castiglia", "Viera", "Valderrama"};

    public String[] getFirstNames() {
        return firstNames;
    }

    public String[] getLastNames() {
        return lastNames;
    }

}
