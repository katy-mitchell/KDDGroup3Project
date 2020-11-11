package com.uncc.kdd;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Control implements Initializable 
{
	private int suppoThreshold;
    private double confiThreshold;
    private List<Rules> certRule = null;
    private List<Rules> possRule = null;
	private List<Integer> stabAttri;
    private int deciAttri;
    private String deciValueF;
    private String deciValueT;
    private List<String> attriNames;
    private String[][] data;
    private List<ActRules> actRules = new ArrayList<>(); 
    private List<Rules> alltheRules = new ArrayList<>();
    private PrintStream mdFile;
    private PrintStream outputFile;
    private String OutPath = "";
    private String GuiInpFile = "";
    private String AttriFile = "";
    private boolean inpLoad = false;
    
    @FXML
    TextField outputDataFolder;
    
    @FXML
    Label inputFileLabel;
    
    @FXML
    Label outputFileLabel;
    
    @FXML
    Label attributeFileLabel;
    
    @FXML
    TextField minimumConfidence;

    @FXML
    ComboBox<String> delimiterOptions;

    @FXML
    TextField minimumSupport;
    
    @FXML
    TextField InitialValue;

    @FXML
    TextField EndValue;
    
    @FXML
    ComboBox<String> decisionAttributeSelection;
    
    @FXML
    ListView<String> stableAttributeSelection;
    
    
   
    // Print a message on the out.txt, out.md files as well as the console
    private void printMess(String msg, String md_message) 
    {
        System.out.println(msg);
        outputFile.println(msg);
        mdFile.println(md_message);
    }

    // The FXMLLoader calls this function when the initialization is complete
    @Override 
    public void initialize(URL fxmlFileLoc, ResourceBundle res) 
    {
        // Setting delimiter options to choose from.
        delimiterOptions.getItems().setAll("comma", "tab", "space");
        delimiterOptions.getSelectionModel().selectFirst();
    }
    
    public void GuiInputFT(ActionEvent actEvent) 
    {
    	System.out.println("Data File Input");
    	FileChooser filechooser = new FileChooser();
    	filechooser.getExtensionFilters().add(new ExtensionFilter("All files","*"));
    	File file = filechooser.showOpenDialog(null);
    	if (file!=null) 
    	{
    		GuiInpFile = file.getAbsolutePath();
    	}
    	System.out.println("file path " + GuiInpFile);
    	inputFileLabel.setText(GuiInpFile);
    }

    // Expects proper functionality of the data and names/attributes files that are selected 
    public void LoaddatasetAndAttributes() 
    {
        inpLoad = false;
        System.out.println("Load Inputs button clicked...");
        FileChooser filechooser = new FileChooser();

        List<String> dataCont = null;
        List<String> nameCont = null;

        try 
        {
            dataCont = Files.readAllLines(Paths.get(GuiInpFile));
            nameCont = Files.readAllLines(Paths.get(AttriFile));
        } 
        catch (IOException e) 
        {
            Alert a = new Alert(Alert.AlertType.ERROR, "Invalid File Input Paths", ButtonType.OK);
            a.showAndWait();
            return;
        }

        // Parsing the data file.
        try 
        {
            String delim = delimiterOptions.getValue();

            if (delim.equals("tab")) 
            {
                delim = "\t";
            } 
            else if (delim.equals("comma")) 
            {
                delim = ",";
            } 
            else 
            {
                delim = " ";
            }

            int numAttr = dataCont.get(0).split(delim).length;

            String[][] dataMat = new String[dataCont.size()][numAttr];

            for (int j = 0; j < dataCont.size(); j++) 
            {
                String[] currentEntry = dataCont.get(j).split(delim);
                dataMat[j] = currentEntry;
            }

            data = dataMat;
        } 
        catch (Exception e) 
        {
            Alert a = new Alert(Alert.AlertType.ERROR, "Unable to read data input file", ButtonType.OK);
            a.showAndWait();
            return;
        }

        // Parsing the name/attribute file.
        try 
        {
            attriNames = nameCont;
            if (attriNames.size() != data[0].length) 
            {
                throw new Exception();
            }
            stableAttributeSelection.getItems().clear();
            // Update list of attributes with attribute names.
            stableAttributeSelection.getItems().addAll(attriNames);

            decisionAttributeSelection.setItems(FXCollections.observableArrayList(attriNames));
        } 
        catch(Exception e) 
        {
            Alert a = new Alert(Alert.AlertType.ERROR, "Unable to read names input file. Please ensure that each name(attribute) is on a newline.", ButtonType.OK);
            a.showAndWait();
            return;
        }

        inpLoad = true;
        System.out.println("Input Files Loaded Successfully!");
    }
    
    public void AttributeFT(ActionEvent actEvent) 
    {
    	System.out.println("Attribute File Input");
    	FileChooser filechooser = new FileChooser();
    	filechooser.getExtensionFilters().add(new ExtensionFilter("All files","*"));
    	File file = filechooser.showOpenDialog(null);
    	if (file!=null) 
    	{
    		AttriFile = file.getAbsolutePath();
    	}
    	System.out.println("file path "+AttriFile);
    	attributeFileLabel.setText(AttriFile);
    }

    public void generateActionRules(ActionEvent actEvent) 
    {
        if (!inpLoad) 
        {
            Alert a = new Alert(Alert.AlertType.ERROR, "Please Load input files!", ButtonType.OK);
            a.showAndWait();
            return;
        }

        alltheRules.clear();
        actRules.clear();

        // Saving the current necessary input from forms.
        try 
        {
            suppoThreshold = Integer.parseInt(minimumSupport.getText());
            confiThreshold = Double.parseDouble(minimumConfidence.getText()) * .01;
        } 
        catch (Exception e) 
        {
            Alert a = new Alert(Alert.AlertType.ERROR, "Please select minimum support and confidence!", ButtonType.OK);
            a.showAndWait();
            return;
        }

        deciAttri = decisionAttributeSelection.getSelectionModel().getSelectedIndex();

        if (deciAttri == -1) 
        {
            Alert a = new Alert(Alert.AlertType.ERROR, "Please select decision attribute!", ButtonType.OK);
            a.showAndWait();
            return;
        }

        stabAttri = stableAttributeSelection.getSelectionModel().getSelectedIndices();

        deciValueF = InitialValue.getText();
        deciValueT = EndValue.getText();

        if (deciValueF.isEmpty() || deciValueT.isEmpty()) 
        {
            Alert a = new Alert(Alert.AlertType.ERROR, "Please specify both the decision value from(initial) and to(end).", ButtonType.OK);
            a.showAndWait();
            return;
        }
        
        try 
        {
            outputFile = new PrintStream(new File(OutPath + File.separator + "out.txt"));
            mdFile = new PrintStream(new File(OutPath + File.separator + "out.md"));
        } 
        catch (IOException e) 
        {
            Alert a = new Alert(Alert.AlertType.ERROR, "Output File Path Invalid", ButtonType.OK);
            a.showAndWait();
            return;
        }

        printMess("\n$$ Computing LERS! $$\n", "# Computing LERS!");
        compLERS(data, deciAttri);
        printMess("\n$$ Computing Action Rules! $$\n", "## Computing Action Rules!");
        compActRules();
        outputFile.close();
        System.exit(0);
    }
    
    public void OutputFT(ActionEvent actEvent) 
    {
    	System.out.println("Output file location");
    	DirectoryChooser directorychooser = new DirectoryChooser();
    	File file = directorychooser.showDialog(null);
    	if (file!=null) 
    	{
    		OutPath = file.getAbsolutePath();
    	}
    	System.out.println("file path "+OutPath);
    	outputFileLabel.setText(OutPath);
    }
    
    // From the LERS certain rules, construct Action Rules from the user input <decision attribute, to value, from value>
    private void compActRules() 
    {
        // Look for elements with From
        // Look for elements with To

        for (int i = 0; i < alltheRules.size(); i++) 
        {
            Rules fRule = alltheRules.get(i);
            if (fRule.deciGroup.attValues.contains(deciValueF)) 
            {
                for (int j = i + 1; j < alltheRules.size(); j++) 
                {
                    Rules tRule = alltheRules.get(j);
                    if (tRule.deciGroup.attValues.contains(deciValueT)) 
                    {
                        // This could be an action rule!
                        // Check if stable attributes are the same. They must be.
                        // Identify flexible attributes that are different.

                        List<String[]> tFPairs = new ArrayList<>();
                        int numFlex = 0;
                        for (int k = 0; k < attriNames.size(); k++) 
                        {
                            String fValue = fRule.attrGroup.attValues.get(k);
                            String tValue = tRule.attrGroup.attValues.get(k);

                            if (fValue == null && tValue == null) 
                            {
                                continue;
                            }


                            if (stabAttri.contains(k)) 
                            {
                                // Ensure they are the same.
                                if (fValue == null) 
                                {
                                    tFPairs.add(new String[] {Integer.toString(k), tValue, tValue});
                                } 
                                else if (tValue == null) 
                                {
                                    tFPairs.add(new String[] {Integer.toString(k), fValue, fValue});
                                } 
                                else if (fValue.equals(tValue)) 
                                {
                                    tFPairs.add(new String[] {Integer.toString(k), fValue, tValue});
                                } 
                                else 
                                {
                                    break;
                                }

                            } 
                            else 
                            {
                                // It's a flexible attribute.
                                if (fValue != null && tValue != null) 
                                {
                                    tFPairs.add(new String[]{Integer.toString(k), fValue, tValue});
                                    numFlex++;
                                }
                            }
                        }

                        if (tFPairs.isEmpty() || numFlex == 0)
                            continue;

                        AttrGroups fRuleDecision = fRule.deciGroup;
                        AttrGroups tRuleDecision = tRule.deciGroup;

                        // Encode decision transformation same way as others.
                        String[] decToFromPair = new String[] 
                        		{
                                Integer.toString(deciAttri),
                                fRuleDecision.attValues.get(deciAttri),
                                tRuleDecision.attValues.get(deciAttri)
                                };

                        // Support = card(From attributes ^ From decision attributes)
                        // Conf = (support / card(From attributes)) * (card(To attributes ^ To decision attributes) / card(To attributes))

                        int suppo = fRule.getSuppo();
                        double confi =
                                ((double) suppo / fRule.attrGroup.getCardi()) *
                                ((double) tRule.getSuppo() / tRule.attrGroup.getCardi());

                        if (suppo < suppoThreshold || confi < confiThreshold)
                            continue;

                        ActRules newActRule = new ActRules(confi, suppo, decToFromPair, attriNames, tFPairs);
                        actRules.add(newActRule);
                    }
                }
            }
        }

        List<ActRules> uniqActRules = new ArrayList<>();
        for(ActRules arule : actRules) 
        {
            if (!uniqActRules.contains(arule)) 
            {
                uniqActRules.add(arule);
            }
        }

        actRules = uniqActRules;

        // All rules have been added. Now print them and save to disk!
        printMess("\n$$ ACTION RULES $$\n", "### ACTION RULES");
        printMess("", "\n\n | RULE | SUPPORT | CONFIDENCE |"+"\n"+"| -- | -- | -- |");
        for(ActRules arule : actRules) {
            printMess(arule.toString(), arule.toMdString());
        }
    }

    // Input: t - n * m Computes the LERS 
    //        d - decision attribute index (between 0 and m - 1)
    private void compLERS(String[][] t, int d) 
    {


        List<AttrGroups> attrGroups = new ArrayList<>();
        List<AttrGroups> deciAttrGroups = new ArrayList<>();

        // attribute level outer loop
        for (int i = 0; i < t[0].length; i++) 
        {
            HashMap<String, Set<Integer>> hm = new HashMap<>();
            Set<String> currentValues = new TreeSet<>();

            // entry level inner loop
            for (int j = 0; j <t.length; j++) 
            {
                String keyStr = t[j][i];

                // unspecified attributes are skipped
                if (keyStr.equals("?"))
                    continue;

                Set<Integer> currValue = hm.get(keyStr);

                if (currValue != null) 
                {
                    // Add entry #.
                    currValue.add(j);
                } 
                else 
                {
                    // Creating new value.
                    Set<Integer> newValue = new TreeSet<>();
                    newValue.add(j);
                    hm.put(keyStr, newValue);
                }
            }

            // Creating set of attribute values
            Set<String> key = hm.keySet();
            for (String k : key) 
            {
                ArrayList<String> attrValues = new ArrayList<>();

                for (int a = 1; a < t[0].length; a++) 
                {
                    attrValues.add(null);
                }

                // Adding current attribute's hashmap to the list and leaving the rest blank. These will be filled later on when merging happens.
                attrValues.add(i, k);

                AttrGroups attributegroups = new AttrGroups(hm.get(k), attrValues);

                // Separating the decision attribute sets from others.
                if (i == d) 
                {
                    deciAttrGroups.add(attributegroups);
                }
                else 
                {
                    attrGroups.add(attributegroups);
                }
            }
        }


        // Checking for the subsets of decision attributes.

        int dSize = 2;
        certRule = new ArrayList<>();
        possRule = new ArrayList<>();

        boolean setRem = true;

        while (setRem) 
        {
            printMess(String.format("\n$$ Iteration %d $$", dSize - 1), String.format("\n ## Iteration %d \n", dSize - 1));

            List<Rules> currentCertRules = new ArrayList<>();
            List<Rules> currentPossRules = new ArrayList<>();

            List<AttrGroups> currentUnmarked = new ArrayList<>();

            for (AttrGroups attGroup : attrGroups) 
            {
                // Checking to see if this attribute group contains a subset group in certain rules.
                // If present then skip this.
                boolean isSubsetMarked = false;
                for (Rules cert : certRule) 
                {
                    if (cert.attrGroup.isSubset(attGroup)) 
                    {
                        isSubsetMarked = true;
                        break;
                    }
                }

                if (isSubsetMarked)
                    continue;

                for (AttrGroups decGroup : deciAttrGroups) 
                {
                    // Finding the intersection of attGroup and decGroup.
                    attGroup.mark = null;
                    // Possible rule.
                    int numberOverLap = 0;
                    for (Integer entry : attGroup.entry) 
                    {
                        if (decGroup.entry.contains(entry)) 
                        {
                            numberOverLap++;
                        }
                    }

                    // Creating rules only for fully or partially overlapping groups.
                    if (numberOverLap == 0)
                        continue;

                    double currentConfi = (double) numberOverLap / attGroup.entry.size();

                    // Support and confidence thresholds must be passed
                    if (numberOverLap < suppoThreshold || currentConfi < confiThreshold)
                        continue;

                    Rules newRules = new Rules(numberOverLap,currentConfi, attriNames, attGroup, decGroup);
                    
                    if (numberOverLap == attGroup.entry.size()) 
                    {
                        currentCertRules.add(newRules);
                    } 
                    else 
                    {
                        currentPossRules.add(newRules);
                        currentUnmarked.add(attGroup);
                    }
                }
            }


            // Rules are created. Combining them from the current possible ones.
            List<AttrGroups> newAttriGroups = new ArrayList<>();
            for (int i = 0; i < currentPossRules.size(); i++) 
            {
                for (int j = i + 1; j < currentPossRules.size(); j++) 
                {
                    // Combining sets. returns null if failed
                	AttrGroups combGroup = AttrGroups.comb(currentUnmarked.get(i), currentUnmarked.get(j), dSize);
                    if (combGroup != null) 
                    {
                        newAttriGroups.add(combGroup);
                    }
                }
            }

            dSize++;

            // New AttributeGroups are created. Removing duplicates from the AttributeGroups
            List<AttrGroups> uniqueAttriGroups = new ArrayList<>();
            for (AttrGroups attri : newAttriGroups) 
            {
                if (!uniqueAttriGroups.contains(attri))
                    uniqueAttriGroups.add(attri);
            }

            List<Rules> uniqCertRules = new ArrayList<>();
            for (Rules rule : currentCertRules) 
            {
                if (!uniqCertRules.contains(rule))
                    uniqCertRules.add(rule);
            }

            List<Rules> uniqPossRules = new ArrayList<>();
            for (Rules rule : currentPossRules) 
            {
                if (!uniqPossRules.contains(rule))
                    uniqPossRules.add(rule);
            }


            attrGroups = uniqueAttriGroups;


            // Printing Certain rules.
            printMess("\n$$ CERTAIN RULES $$", "### CERTAIN RULES \n");
            printMess("", "\n\n | RULE | SUPPORT | CONFIDENCE |"+"\n"+"| -- | -- | -- |");
            for(Rules cert : currentCertRules) 
            {
                printMess(cert.toString(), cert.toMdString());
            }

            // Printing all Possible rules with support and confidence values.
            printMess("\n$$ POSSIBLE RULES $$", "### POSSIBLE RULES");
            printMess("", "\n\n | RULE | SUPPORT | CONFIDENCE |"+"\n"+"| -- | -- | -- |");
            for(Rules poss : currentPossRules) 
            {
                printMess(poss.toString(), poss.toMdString());
            }

            certRule.addAll(uniqCertRules);
            possRule.addAll(uniqPossRules);

            setRem = !uniqueAttriGroups.isEmpty();
        }

        printMess("Extraction cannot be done anymore!", "<br> `Extraction cannot be done anymore!`");
        alltheRules.addAll(certRule);
        alltheRules.addAll(possRule);

    }
}
