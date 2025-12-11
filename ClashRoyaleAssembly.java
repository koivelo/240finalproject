    package mars.mips.instructions.customlangs;
    import mars.simulator.*;
    import mars.mips.hardware.*;
    import mars.*;
    import mars.util.*;
    import mars.mips.instructions.*;
    import java.util.Random;

public class ClashRoyaleAssembly extends CustomAssembly {
    
    public ClashRoyaleAssembly() {
        super();
    }
    
    @Override
    public String getName(){
        return "Clash Royale Assembly";
    }

    @Override
    public String getDescription(){
        return "Assembly language to command your troops in Clash Royale";
    }

    @Override
    protected void populate(){

        //  -------- UNIQUE INSTRUCTIONS --------
        instructionList.add(
            new BasicInstruction("elpu $t0,10",
            "Gain Elixir: increase elixir count in $t0 by immediate value",
            BasicInstructionFormat.I_FORMAT,
            "001000 fffff 00000 ssssssssssssssss",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                int[] operands = statement.getOperands();
                int elixir = RegisterFile.getValue(operands[0]);
                int gain = operands[1] << 16 >> 16;
                int newElixir = elixir + gain;
                RegisterFile.updateRegister(operands[0], newElixir);
                }
            }));

        instructionList.add(
            new BasicInstruction("dep $t0, $t1",
            "Deploy Troop: deploy troop with ID in $t1 if enough elixir in $t0",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff sssss 00000 00000 101010",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                    int[] operands = statement.getOperands();
                    int elixir = RegisterFile.getValue(operands[0]);
                    int troopID = RegisterFile.getValue(operands[1]);
                    int troopCost = getTroopCost(troopID);
                    String troop = getTroop(troopID);
                    if (troopCost == -1) {
                        throw new ProcessingException(statement,
                            "invalid troop ID");
                    }
                    if (elixir >= troopCost) {
                        elixir -= troopCost;
                        RegisterFile.updateRegister(operands[0], elixir);
                        SystemIO.printString("Deployed " + troop + " (ID " + troopID + ")\n");
                    } else {
                        SystemIO.printString("Not enough elixir to deploy " + troop + " (ID " + troopID + ")\n");
                    }
                    }
                }));

        instructionList.add(
            new BasicInstruction("heheheha",
            "Emote: a laugh at your opponent",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 00000 00000 101011",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                    SystemIO.printString("😂😂😂 heheheha! 😂😂😂\n");
                    }
                    }));

        instructionList.add(
            new BasicInstruction("rage $t0",
            "Rage Spell: double elixir generation rate stored in $t0",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 fffff 00000 101100",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                    int[] operands = statement.getOperands();
                    int elixir = RegisterFile.getValue(operands[0]);
                    int newElixir = elixir * 2;
                    RegisterFile.updateRegister(operands[0], newElixir);
                    SystemIO.printString("Rage spell activated! Elixir generation rate doubled to " + newElixir + "\n");
                    }
                }));

        instructionList.add(
            new BasicInstruction("push $t1",
            "Push: push your troop with ID in $t1 towards enemy tower and delete the troop",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 fffff 00000 101101",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                    int[] operands = statement.getOperands();
                    int troopID = RegisterFile.getValue(operands[0]);
                    String troop = getTroop(troopID);
                    RegisterFile.updateRegister(operands[0], 0); // Delete the troop by setting its ID to 0
                    SystemIO.printString(troop + " (ID " + troopID + ") is pushing towards the enemy tower!\n");
                    }
                }));


        instructionList.add(
            new BasicInstruction("def $t0, $t1, $t2",
            "Defend: using troop with ID in $t1 to defend against enemy troop with ID in $t2, elixir in $t0",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff fffff sssss 00000 101110",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                    int[] operands = statement.getOperands();
                    int elixir = RegisterFile.getValue(operands[0]);
                    int defenderID = RegisterFile.getValue(operands[1]);
                    int enemyID = RegisterFile.getValue(operands[2]);
                    String enemy = getTroop(enemyID);
                    int defenderCost = getTroopCost(defenderID);
                    String defender = getTroop(defenderID);
                    if (defenderCost == -1) {
                        throw new ProcessingException(statement,
                        "invalid defender troop ID");
                    }
                    if (elixir >= defenderCost) {
                        elixir -= defenderCost;
                        RegisterFile.updateRegister(operands[0], elixir);
                        SystemIO.printString(defender + " (ID " + defenderID + ") successfully defended against " + enemy + " (ID " + enemyID + ")\n");
                    } else {
                        SystemIO.printString("Not enough elixir to deploy " + defender + " (ID " + defenderID + ") for defense against " + enemy + " (ID " + enemyID + ")\n");
                    }
                }
            }));
            
            instructionList.add(
                new BasicInstruction("enemydeploy $t2",
                "Enemy Deploy: simulate enemy deploying a random troop, storing troop ID in $t2",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 fffff 00000 101111",
                new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                    int[] operands = statement.getOperands();
                    Random rand = new Random();
                    int troopID = rand.nextInt(9) + 1; // Troop IDs 1-9
                    RegisterFile.updateRegister(operands[0], troopID);
                    String troop = getTroop(troopID);
                    SystemIO.printString("Enemy deployed " + troop + " (ID " + troopID + ")\n");
                    }
                }));

            instructionList.add(
                new BasicInstruction("th $t1",
                "Tower Hit: simulate hitting enemy tower with troop ID in $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 fffff 00000 110000",
                new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                    int[] operands = statement.getOperands();
                    int troopID = RegisterFile.getValue(operands[0]);
                    String troop = getTroop(troopID);
                    SystemIO.printString(troop + " (ID " + troopID + ") hit the enemy tower!\n");
                    }
                }));

        instructionList.add(
                new BasicInstruction("bar $t0, label",
                "Goblin Barrel : Jump to statement at label's address if ($t0) have enough elixir (3 or higher) to deploy a goblin barrel",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000100 fffff 00000 ssssssssssssssss",
                new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                
                    if (RegisterFile.getValue(operands[0]) >= 3)
                    {
                        Globals.instructionSet.processBranch(operands[1]);
                    }
                }
            }));

        instructionList.add(
            new BasicInstruction("openchest $t1",
            "Open Chest: open a chest to gain a troop, storing troop ID in $t1",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 fffff 00000 110001",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                int[] operands = statement.getOperands();
                Random rand = new Random();
                int troopID = rand.nextInt(9) + 1; // Troop IDs 1-9
                RegisterFile.updateRegister(operands[0], troopID);
                String troop = getTroop(troopID);
                SystemIO.printString("You opened a chest and received " + troop + " (ID " + troopID + ")\n");
                }
            }));
            

            //  -------- BASIC INSTRUCTIONS --------
            instructionList.add(
            new BasicInstruction("gen $t0, $t1, $t2",
            "Addition: you generate elixir, $t0 = $t1 + $t2",
            BasicInstructionFormat.R_FORMAT,
            "000000 sssss ttttt fffff 00000 100000",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    int add1 = RegisterFile.getValue(operands[1]);
                    int add2 = RegisterFile.getValue(operands[2]);
                    int sum = add1 + add2;
                    RegisterFile.updateRegister(operands[0], sum);
                }
            }));

        instructionList.add(
            new BasicInstruction("leak $t0, $t1, $t2",
            "Subtraction: you leak elixir, $t0 = $t1 - $t2",
            BasicInstructionFormat.R_FORMAT,
            "000000 sssss ttttt fffff 00000 100001",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    int sub1 = RegisterFile.getValue(operands[1]);
                    int sub2 = RegisterFile.getValue(operands[2]);
                    int diff = sub1 - sub2;
                    RegisterFile.updateRegister(operands[0], diff);
                }
            }));

        instructionList.add(
            new BasicInstruction("pump $t0, $t1",
            "Multiplication: use an elixir pump and multiply $t0 by $t1, store result in HI/LO",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff sssss 00000 00000 011000",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    long val1 = RegisterFile.getValue(operands[0]);
                    long val2 = RegisterFile.getValue(operands[1]);
                    long product = val1 * val2;
                    RegisterFile.updateRegister("hi", (int)(product >> 32));
                    RegisterFile.updateRegister("lo", (int)(product & 0xFFFFFFFFL));
                }
            }));

        instructionList.add(
            new BasicInstruction("tax $t0, $t1",
            "Division: the kings tax you and divide $t0 by $t1, quotient in LO, remainder in HI",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff sssss 00000 00000 011010",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    int val1 = RegisterFile.getValue(operands[0]);
                    int val2 = RegisterFile.getValue(operands[1]);
                    if (val2 == 0) {
                        throw new ProcessingException(statement, "Division by zero");
                    }
                    int quotient = val1 / val2;
                    int remainder = val1 % val2;
                    RegisterFile.updateRegister("lo", quotient);
                    RegisterFile.updateRegister("hi", remainder);
                }
            }));
        
        instructionList.add(
            new BasicInstruction("col $t0",
            "Collect From HI: move the value from HI register to $t0",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 fffff 00000 010000",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    int hiValue = RegisterFile.getValue(32); // HI register number
                    RegisterFile.updateRegister(operands[0], hiValue);
                }
            }
            )
        );

        instructionList.add(
            new BasicInstruction("st $t0",
            "Steal from LO: move the value from LO register to $t0",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 fffff 00000 010010",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    int loValue = RegisterFile.getValue(33); // LO register number
                    RegisterFile.updateRegister(operands[0], loValue);
                }
            }
            )
        );

        instructionList.add(
            new BasicInstruction("min label",
            "Miner: jump to the statement at the given label",
            BasicInstructionFormat.J_FORMAT,
            "000010 ffffffffffffffffffffffffff",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    Globals.instructionSet.processJump(operands[0]);
                }
            }
            )
        );

        instructionList.add(
            new BasicInstruction("nado label",
            "Tornado Spell: shift yourself to the given label and store return address in $ra",
            BasicInstructionFormat.J_FORMAT,
            "000011 ffffffffffffffffffffffffff",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(31, RegisterFile.getProgramCounter() + 4); // $ra is register 31
                    Globals.instructionSet.processJump(operands[0]);
                }
            }
            )
        );

        instructionList.add(
            new BasicInstruction("ret $ra",
            "Return: return from where you were tornadoed to address contained in $ra",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff 00000 00000 00000 001000",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    int targetAddress = RegisterFile.getValue(operands[0]);
                    Globals.instructionSet.processJump(targetAddress);
                }
            }
            )
        );

        instructionList.add(
            new BasicInstruction("mir $t0, $t1",
            "Mirror spell: copy the value from $t1 to $t0",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 sssss fffff 00000 001001",
            new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
                {
                    int[] operands = statement.getOperands();
                    int value = RegisterFile.getValue(operands[1]);
                    RegisterFile.updateRegister(operands[0], value);
                }
            }
            )
        );




        


}

private String getTroop(int troopID) {
    switch (troopID) {
        case 1: return "Skeletons";
        case 2: return "Goblins";
        case 3: return "Knight";
        case 4: return "Hog Rider";
        case 5: return "Wizard";
        case 6: return "Boss Bandit";
        case 7: return "Mega Knight";
        case 8: return "Golem";
        case 9: return "3 Musketeers";
        default: return "Unknown Troop";
    }
}

private int getTroopCost(int troopID) {
    switch (troopID) {
        case 1: return 1; // Skeletons
        case 2: return 2; // Goblins
        case 3: return 3; // Knight
        case 4: return 4; // Hog Rider
        case 5: return 5; // Wizard
        case 6: return 6; // Boss Bandit
        case 7: return 7; // Mega Knight
        case 8: return 8; // Golem
        case 9: return 9; // 3 Musketeers
        default: return -1; // Invalid troop ID
    }
}

    
}
