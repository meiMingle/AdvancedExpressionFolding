package com.intellij.advancedExpressionFolding.extension;

import com.intellij.advancedExpressionFolding.AdvancedExpressionFoldingSettings;
import com.intellij.advancedExpressionFolding.UnexpectedException;
import com.intellij.advancedExpressionFolding.expression.*;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BuildExpressionExt {
    static final Map<String, Key<CachedValue<Expression>>> KEY_MAP = new WeakHashMap<>();
    static final FoldingDescriptor[] NO_DESCRIPTORS = new FoldingDescriptor[0];


    // 💩💩💩 Define the AdvancedExpressionFoldingProvider extension point
    @Contract("_, _, true -> !null")
    static Expression buildExpression(@NotNull PsiElement element, @NotNull Document document, boolean synthetic) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (element instanceof PsiForStatement) {
            Expression expression = ForStatementExpressionExt.getForStatementExpression((PsiForStatement) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiForeachStatement) {
            Expression expression = LoopExt.getForeachStatementExpression((PsiForeachStatement) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiIfStatement) {
            Expression expression = IfExt.getIfExpression((PsiIfStatement) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiWhileStatement) {
            Expression expression = LoopExt.getWhileStatement((PsiWhileStatement) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiJavaToken && ((PsiJavaToken) element).getTokenType() == JavaTokenType.SEMICOLON
                && settings.getState().getSemicolonsCollapse()
                && !element.isWritable()) {
            return new SemicolonExpression(element, element.getTextRange());
        }
        if (element instanceof PsiCatchSection) {
            Expression expression = null;
            AdvancedExpressionFoldingSettings settings1 = AdvancedExpressionFoldingSettings.getInstance();
            if (((PsiCatchSection) element).getParameter() != null
                    && ((PsiCatchSection) element).getLParenth() != null && ((PsiCatchSection) element).getRParenth() != null
                    && settings1.getState().getCompactControlFlowSyntaxCollapse()) {
                expression = new CompactControlFlowExpression((PsiCatchSection) element,
                        TextRange.create(((PsiCatchSection) element).getLParenth().getTextRange().getStartOffset(),
                                ((PsiCatchSection) element).getRParenth().getTextRange().getEndOffset()));
            }
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiDoWhileStatement) {
            Expression expression = LoopExt.getDoWhileStatement((PsiDoWhileStatement) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiSwitchStatement) {
            Expression expression = IfExt.getSwitchStatement((PsiSwitchStatement) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiArrayAccessExpression && settings.getState().getGetExpressionsCollapse()) {
            Expression expression = PsiArrayAccessExpressionExt.getArrayAccessExpression((PsiArrayAccessExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiMethodCallExpression) {
            Expression expression = MethodCallExpressionExt.getMethodCallExpression((PsiMethodCallExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiReferenceExpression) {
            Expression expression = ReferenceExpressionExt.getReferenceExpression((PsiReferenceExpression) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiNewExpression) {
            Expression expression = NewExpressionExt.getNewExpression((PsiNewExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiLiteralExpression) {
            Expression expression = LiteralExpressionExt.getLiteralExpression((PsiLiteralExpression) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiAssignmentExpression) {
            Expression expression = AssignmentExpressionExt.getAssignmentExpression((PsiAssignmentExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiPolyadicExpression) {
            Expression expression = IfExt.getPolyadicExpression((PsiPolyadicExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiBinaryExpression) {
            Expression expression = BinaryExpressionExt.getBinaryExpression((PsiBinaryExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiConditionalExpression) {
            Expression expression = IfExt.getConditionalExpression((PsiConditionalExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiPrefixExpression) {
            Expression expression = PrefixExpressionExt.getPrefixExpression((PsiPrefixExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiParenthesizedExpression
                && settings.getState().getCastExpressionsCollapse()) {
            if (((PsiParenthesizedExpression) element).getExpression() instanceof PsiTypeCastExpression e) {
                TypeCast typeCast = PsiTypeCastExpressionExt.getTypeCastExpression(e, document);
                if (typeCast != null) {
                    return new TypeCast(element, element.getTextRange(), typeCast.getObject());
                }
            }
            @Nullable PsiExpression e = ((PsiParenthesizedExpression) element).getExpression();
            if (e != null) {
                @Nullable Expression expression = getExpression(e, document, synthetic);
                if (expression != null) {
                    return expression;
                }
            }
        }
        if (element instanceof PsiTypeCastExpression
                && settings.getState().getCastExpressionsCollapse()) {
            TypeCast expression = PsiTypeCastExpressionExt.getTypeCastExpression((PsiTypeCastExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiDeclarationStatement st) {
            Expression expression = PsiDeclarationStatementEx.createExpression(st, document);
            if (expression != null) {
                return expression;
            }
        }

        if (settings.getState().getVarExpressionsCollapse()
                && element instanceof PsiVariable
                && (element.getParent() instanceof PsiDeclarationStatement
                || element.getParent() instanceof PsiForeachStatement)) {
            Expression expression = PsiVariableExt.getVariableDeclaration((PsiVariable) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiCodeBlock) {
            Expression expression = PsiCodeBlockExt.getCodeBlockExpression((PsiCodeBlock) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiClass psiClass) {
            Expression expression = PsiClassExt.createExpression(psiClass);
            if (expression != null) {
                return expression;
            }
        }

        if (synthetic) {
            ArrayList<Expression> children = new ArrayList<>();
            Helper.findChildExpressions(element, children, document);
            return new SyntheticExpressionImpl(element, element.getTextRange(), document.getText(element.getTextRange()), children);
        }
        return null;
    }

    @Contract("_, _, true -> !null")
    static Expression getExpression(@NotNull PsiElement element, @NotNull Document document, boolean synthetic) {
        return CachedValuesManager.getCachedValue(element, getKey(document, synthetic),
                () -> CachedValueProvider.Result.create(buildExpression(element, document, synthetic), PsiModificationTracker.MODIFICATION_COUNT)
        );
    }

    static Key<CachedValue<Expression>> getKey(Document document, boolean synthetic) {
        var key = document.hashCode() + " " + (synthetic ? 1 : 0);
        return KEY_MAP.computeIfAbsent(key, Key::create);
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static Expression getAnyExpression(@NotNull PsiElement element, @Nullable Document document) throws IndexNotReadyException {
        //noinspection ConstantConditions
        return getExpression(element, document, true);
    }

    /**
     * TODO: Think how we can prevent IndexNotReadyException (e.g. via "is dumb mode")
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public static Expression getNonSyntheticExpression(@NotNull PsiElement element, @Nullable Document document) throws IndexNotReadyException {
        //noinspection ConstantConditions
        return getExpression(element, document, false);
    }

    @NotNull
    public static FoldingDescriptor @NotNull [] collectFoldRegionsRecursively(@NotNull PsiElement element, @NotNull Document document, boolean quick) {
        PsiElement lastElement = element;
        List<FoldingDescriptor> allDescriptors = null;
        try {
            @Nullable Expression expression = getNonSyntheticExpression(element, document);
            if (expression != null && expression.supportsFoldRegions(document, null)) {
                FoldingDescriptor[] descriptors = expression.buildFoldRegions(expression.getElement(), document, null);
                allDescriptors = new ArrayList<>();
                Collections.addAll(allDescriptors, descriptors);
            }
            if (expression == null || expression.isNested()) {
                for (PsiElement child : element.getChildren()) {
                    lastElement = child;
                    FoldingDescriptor[] descriptors = collectFoldRegionsRecursively(child, document, quick);
                    if (descriptors.length > 0) {
                        if (allDescriptors == null) {
                            allDescriptors = new ArrayList<>();
                        }
                        Collections.addAll(allDescriptors, descriptors);
                    }
                }
            }
        } catch (IndexNotReadyException | ProcessCanceledException ignore) {
        } catch (UnexpectedException e) {
            throw e;
        } catch (Exception e) {
            String fileName = null;
            PsiFile containingFile = lastElement.getContainingFile();
            if (containingFile != null) {
                fileName = containingFile.getName();
            }
            throw new UnexpectedException(lastElement.getClass(), lastElement.getText(), lastElement.getTextRange(), fileName, e);
        }
        return allDescriptors != null ? allDescriptors.toArray(NO_DESCRIPTORS) : NO_DESCRIPTORS;
    }

}
