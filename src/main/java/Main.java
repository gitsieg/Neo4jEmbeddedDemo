import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

public class Main {
    // Filen til databasen man ønsker å benytte
    private static final File DATABASE_DIRECTORY = new File("/var/lib/neo4j/data/databases/nydb.db");

    // Nodetyper som man kan bruke i grafen
    public enum NodeType implements Label {
        Person,
        Car
    }
    // Forhold som man kan bruke i grafen
    public enum RelationType implements RelationshipType {
        Knows,
        Owns,
        HasParent,
        ParentOf
    }

    public static void main(String[] args) {
        // Opprett databasen. Dersom den ikke eksisterer, opprettes en ny
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseBuilder dbBuilder = dbFactory.newEmbeddedDatabaseBuilder(DATABASE_DIRECTORY);
        GraphDatabaseService grapdb = dbBuilder.newGraphDatabase();

        try (Transaction tx = grapdb.beginTx()) {
            grapdb.execute("match (n) detach delete n;");

            // Opprett en bil-node og angi egenskaper
            Node saab = grapdb.createNode(NodeType.Car);
            saab.setProperty("regnr", "RH35665");
            saab.setProperty("model", "9-3");
            saab.setProperty("førstegangsreg", "19990406");

            // Opprett en bil-node og angi egenskaper
            Node honda = grapdb.createNode(NodeType.Car);
            honda.setProperty("regnr", "NM12345");
            honda.setProperty("model", "CR-V");
            honda.setProperty("førstegangsreg", "20180609");

            // Opprett Person-node og angi egenskaper
            Node ola = grapdb.createNode(NodeType.Person);
            ola.setProperty("fornavn", "Ola");
            ola.setProperty("etternavn", "Nordmann");
            ola.setProperty("alder", 45);

            // Opprett Person-node og angi egenskaper
            Node kari = grapdb.createNode(NodeType.Person);
            kari.setProperty("fornavn", "Kari");
            kari.setProperty("etternavn", "Nordmann");
            kari.setProperty("alder", 43);

            // Opprett Person-node og angi egenskaper
            Node per = grapdb.createNode(NodeType.Person);
            per.setProperty("fornavn", "Per");
            per.setProperty("etternavn", "Nordmann");
            per.setProperty("alder", 19);

            // Opprett Person-node og angi egenskaper
            Node stine = grapdb.createNode(NodeType.Person);
            stine.setProperty("fornavn", "Stine");
            stine.setProperty("etternavn", "Nordmann");
            stine.setProperty("alder", 17);

            // Opprett forhold mellom personer og biler
            ola.createRelationshipTo(saab, RelationType.Owns);
            kari.createRelationshipTo(honda, RelationType.Owns);

            // Opprett forhold mellom personer
            kari.createRelationshipTo(ola, RelationType.Knows);
            ola.createRelationshipTo(kari, RelationType.Knows);
            stine.createRelationshipTo(kari, RelationType.HasParent);
            stine.createRelationshipTo(ola, RelationType.HasParent);
            per.createRelationshipTo(kari, RelationType.HasParent);
            per.createRelationshipTo(ola, RelationType.HasParent);
            ola.createRelationshipTo(stine, RelationType.ParentOf);
            ola.createRelationshipTo(per, RelationType.ParentOf);
            kari.createRelationshipTo(stine, RelationType.ParentOf);
            kari.createRelationshipTo(per, RelationType.ParentOf);

            tx.success();
            tx.close();
        }
        // Lukk instansen av grafdatabasen
        grapdb.shutdown();
    }
}
