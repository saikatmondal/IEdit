### Automatic Assistance to Mitigate Inconsistencies in Stack Overflow Rollback Edits

noCode=read.csv("E:/Projects/SOQuestionsNeedCode/ResultsOfDataAnalysis/NoCode/nocode_accepted_answer_delay.csv", header = T)
codeAfterComment=read.csv("E:/Projects/SOQuestionsNeedCode/ResultsOfDataAnalysis/CodeAfterComment/codeaftercomment_accepted_answer_delay.csv", header = T)
codeDuringSubmission = read.csv("E:/Projects/SOQuestionsNeedCode/ResultsOfDataAnalysis/CodeDuringSubmission/codeduringsubmission_accepted_answer_delay.csv", header = T)

#-----------------------------------------------------------------
# Delays of accepted & best answers for the questions with no code
#-----------------------------------------------------------------

  # Accepted answer of noCode
  #--------------------------

  noCode_AccAnsDelay=noCode$timeDelayAcceptedAns
  
  # Remove outliers
  noCode_AccAnsDelayBoxPlotStat = boxplot(noCode_AccAnsDelay)
  low_nc_acc = noCode_AccAnsDelayBoxPlotStat$stats[1]
  up_nc_acc = noCode_AccAnsDelayBoxPlotStat$stats[5]
  noCode_AccAnsDelay = noCode_AccAnsDelay[noCode_AccAnsDelay>low_nc_acc & noCode_AccAnsDelay<up_nc_acc]
  
  # Summary print accepted answer delay
  summary(noCode_AccAnsDelay)
  

  # Best answer of noCOde
  #----------------------
  
  noCode_BestAnsDelay=noCode$timeDelayBestAns
  
  # Remove outliers
  noCode_BestAnsDelayBoxPlotStat = boxplot(noCode_BestAnsDelay)
  low_nc_best = noCode_BestAnsDelayBoxPlotStat$stats[1]
  up_nc_best = noCode_BestAnsDelayBoxPlotStat$stats[5]
  noCode_AccAnsDelay = noCode_BestAnsDelay[noCode_BestAnsDelay>low_nc_best & noCode_BestAnsDelay<up_nc_best]
  
  # Summary print best answer delay 
  summary(noCode_AccAnsDelay)

#-----------------------------------------------------------------------------------------------------------------------
# Delays of accepted & best answers for the questions with code after comment (with respect to question submission time)
#-----------------------------------------------------------------------------------------------------------------------

  # Accepted answer of codeAfterComment
  #------------------------------------
  
  codeAfterComment_AccAnsDelay=codeAfterComment$timeDelayAcceptedAns
  
  # Remove outliers
  codeAfterComment_AccAnsDelayBoxPlotStat = boxplot(codeAfterComment_AccAnsDelay)
  low_cac_acc = codeAfterComment_AccAnsDelayBoxPlotStat$stats[1]
  up_cac_acc = codeAfterComment_AccAnsDelayBoxPlotStat$stats[5]
  codeAfterComment_AccAnsDelay = codeAfterComment_AccAnsDelay[codeAfterComment_AccAnsDelay>low_cac_acc & codeAfterComment_AccAnsDelay<up_cac_acc]
  
  # Summary print best answer delay
  summary(codeAfterComment_AccAnsDelay)
  
  
  # Best answer of codeAfterComment
  #------------------------------------
  
  codeAfterComment_BestAnsDelay=codeAfterComment$timeDelayBestAns
  
  # Remove outliers
  codeAfterComment_BestAnsDelayBoxPlotStat = boxplot(codeAfterComment_BestAnsDelay)
  low_cac_acc = codeAfterComment_BestAnsDelayBoxPlotStat$stats[1]
  up_cac_acc = codeAfterComment_BestAnsDelayBoxPlotStat$stats[5]
  codeAfterComment_AccAnsDelay = codeAfterComment_AccAnsDelay[codeAfterComment_AccAnsDelay>low_cac_acc & codeAfterComment_AccAnsDelay<up_cac_acc]
  
  # Summary print best answer delay
  summary(codeAfterComment_BestAnsDelay)

#-----------------------------------------------------------------------------------------------------------------
# Delays of accepted & best answers for the questions with code after comment (with respect to code addition time)
#-----------------------------------------------------------------------------------------------------------------
  
  # Accepted answer of codeAfterComment
  #------------------------------------
  
  codeAfterComment_wrtca_AccAnsDelay=codeAfterComment$timeDelayAcceptedAnsAfterAddingCode
  
  # Remove outliers
  codeAfterComment_AccAnsDelayBoxPlotStat = boxplot(codeAfterComment_wrtca_AccAnsDelay)
  low_cac_acc = codeAfterComment_AccAnsDelayBoxPlotStat$stats[1]
  up_cac_acc = codeAfterComment_AccAnsDelayBoxPlotStat$stats[5]
  codeAfterComment_wrtca_AccAnsDelay = codeAfterComment_wrtca_AccAnsDelay[codeAfterComment_wrtca_AccAnsDelay>low_cac_acc & codeAfterComment_wrtca_AccAnsDelay<up_cac_acc]
  
  # Summary print best answer delay
  summary(codeAfterComment_wrtca_AccAnsDelay)
  
  
  # Best answer of codeAfterComment
  #------------------------------------
  
  codeAfterComment_wrtca_BestAnsDelay=codeAfterComment$timeDelayBestAnsAfterAddingCode
  
  # Remove outliers
  codeAfterComment_BestAnsDelayBoxPlotStat = boxplot(codeAfterComment_wrtca_BestAnsDelay)
  low_cac_acc = codeAfterComment_BestAnsDelayBoxPlotStat$stats[1]
  up_cac_acc = codeAfterComment_BestAnsDelayBoxPlotStat$stats[5]
  codeAfterComment_wrtca_BestAnsDelay = codeAfterComment_wrtca_BestAnsDelay[codeAfterComment_wrtca_BestAnsDelay>low_cac_acc & codeAfterComment_wrtca_BestAnsDelay<up_cac_acc]
  
  # Summary print best answer delay
  summary(codeAfterComment_wrtca_BestAnsDelay)

#---------------------------------------------------------------------------------------------------------------------------
# Delays of accepted & best answers for the questions with code during submission (with respect to question submission time)
#---------------------------------------------------------------------------------------------------------------------------

  # Accepted answer of codeDuringSubmission
  #----------------------------------------
  
  CodeDuringSubmission_AccAnsDelay=codeDuringSubmission$timeDelayAcceptedAns
  
  # Remove outliers
  CodeDuringSubmission_AccAnsDelayBoxPlotStat = boxplot(CodeDuringSubmission_AccAnsDelay)
  low_cds_acc = CodeDuringSubmission_AccAnsDelayBoxPlotStat$stats[1]
  up_cds_acc = CodeDuringSubmission_AccAnsDelayBoxPlotStat$stats[5]
  CodeDuringSubmission_AccAnsDelay = CodeDuringSubmission_AccAnsDelay[CodeDuringSubmission_AccAnsDelay>low_cds_acc & CodeDuringSubmission_AccAnsDelay<up_cds_acc]
  
  # Summary print best answer delay
  summary(CodeDuringSubmission_AccAnsDelay)
  
  
  # Best answer of codeAfterComment
  #------------------------------------
  
  CodeDuringSubmission_BestAnsDelay=codeDuringSubmission$timeDelayBestAns
  
  # Remove outliers
  CodeDuringSubmission_BestAnsDelayBoxPlotStat = boxplot(CodeDuringSubmission_BestAnsDelay)
  low_cds_acc = CodeDuringSubmission_BestAnsDelayBoxPlotStat$stats[1]
  up_cds_acc = CodeDuringSubmission_BestAnsDelayBoxPlotStat$stats[5]
  CodeDuringSubmission_BestAnsDelay = CodeDuringSubmission_BestAnsDelay[CodeDuringSubmission_BestAnsDelay>low_cds_acc & CodeDuringSubmission_BestAnsDelay<up_cds_acc]
  
  # Summary print best answer delay
  summary(CodeDuringSubmission_BestAnsDelay)
