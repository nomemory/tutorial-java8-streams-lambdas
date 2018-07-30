package net.andreinc.jlands.hr;

import net.andreinc.mockneat.MockNeat;

import java.util.List;

public class Example01 {
    public static void main(String[] args) {
        MockNeat m = MockNeat.threadLocal();

        List<Manager> managers = m.filler(() -> new Manager())
                                  .setter(Manager::setId, m.longSeq())
                                  .setter(Manager::setDepartment, m.departments())
                                  .setter(Manager::setName, m.names().full())
                                  .list(1000)
                                  .val();

        
    }
}
