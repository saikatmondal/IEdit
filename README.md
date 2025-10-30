# Automatic Assistance to Mitigate Rollback Inconsistencies in Collaborative Edits

**Abstract:** The success of technical Q&A sites such as Stack Overflow depends on two key factors: (a) active user participation and (b) the quality of the shared knowledge. Stack Overflow introduced an edit system that allows users to suggest improvements to posts (i.e., questions and answers) to enhance the quality of the content. However, users, such as post owners or site moderators, can reject these suggested edits by rollbacks due to unsatisfactory, low-quality edits or violating edit guidelines. Unfortunately, subjectivity bias in determining whether an edit is satisfactory or unsatisfactory can lead to inconsistencies in the rollback decisions. For example, one user might accept the formatting of a method name (e.g., getActivity()) as a code term, while another might reject it. Such inconsistencies can demotivate and frustrate users whose edits are rejected. Furthermore, several post owners prefer to keep their content unchanged and even resist necessary edits. As a result, they sometimes roll back necessary edits and revert posts to a flawed version, which violates editing guidelines. The problems mentioned above are further compounded by the lack of specific guidelines and tools to assist users in ensuring consistency in user rollback actions. In this study, we investigate the types, prevalence, and impact of rollback edit inconsistencies and propose a solution to address them. The outcomes of this research are fivefold. First, we manually investigated 764 rollback edits (382 questions + 382 answers) and identified eight types of inconsistent rollback. Second, we surveyed 44 practitioners to assess the impact of rollback inconsistencies. More than 80% of the participants found our identified inconsistency types detrimental to post quality. Third, we developed rule-based algorithms and Machine Learning (ML) models to detect the eight types of rollback inconsistencies. Both approaches achieve over 90% accuracy. Fourth, we introduced a tool, iEdit, which integrates these algorithms into a browser extension and assists Stack Overflow users during their edits. Fifth, we surveyed 16 Stack Overflow users to evaluate the effectiveness of iEdit. The participants found the tool’s suggestions helpful in avoiding inconsistent rollback edits. 


## 📂 Project Directory Overview

- **Manually Analyzed Catalog**  
  The manually analyzed catalog can be found in the **`Manual Catalog`** directory.

- **Machine Learning Models and Datasets**  
  The machine learning models and training and test datasets can be found in the **`Machine Learning/Models and Datasets`** directory.

- **Performance Validation**  
  Performance validation details for the rule-based algorithm can be found in the **`Performance Validation of Rule-Based Algorithm`** directory.

- **Survey Data**  
  Survey data with user demographics can be found in the **`Survey`** directory.

## 🧩 iEdit Browser Plugin Integration

The userscripts can be found in the **`Tampermonkey Userscripts`** directory.

### 🧭 Setup Instructions

1. **Install Tampermonkey Extension**  
   Add the **Tampermonkey** extension to your web browser.

2. **Add the Userscripts**  
   Open the Tampermonkey dashboard and add the following scripts:  
   - **`iEdit.txt`** — for the **iEdit** button  
   - **`CheckIn.txt`** — for the **CheckIn** button  

3. **Clone the Repository**  
   ```bash
   git clone https://github.com/saikatmondal/IEdit.git

4. **Run the Local Server**
Launch the local Java server by running:

src/org/srlab/usask/iedit/inconsistencydetector/IEditPluginMain.java

### Using the Plugin

Click the **iEdit** button to open the editing interface of Stack Overflow posts.

After editing, click the **CheckIn** button.
The detected inconsistencies and their rejection probabilities will appear in a text box just below the post.


