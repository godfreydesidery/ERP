<Global.Microsoft.VisualBasic.CompilerServices.DesignerGenerated()> _
Partial Class frmPrinters
    Inherits System.Windows.Forms.Form

    'Form overrides dispose to clean up the component list.
    <System.Diagnostics.DebuggerNonUserCode()> _
    Protected Overrides Sub Dispose(ByVal disposing As Boolean)
        Try
            If disposing AndAlso components IsNot Nothing Then
                components.Dispose()
            End If
        Finally
            MyBase.Dispose(disposing)
        End Try
    End Sub

    'Required by the Windows Form Designer
    Private components As System.ComponentModel.IContainer

    'NOTE: The following procedure is required by the Windows Form Designer
    'It can be modified using the Windows Form Designer.  
    'Do not modify it using the code editor.
    <System.Diagnostics.DebuggerStepThrough()> _
    Private Sub InitializeComponent()
        Me.Panel1 = New System.Windows.Forms.Panel()
        Me.Panel2 = New System.Windows.Forms.Panel()
        Me.fiscalDrawer = New System.Windows.Forms.TextBox()
        Me.Label8 = New System.Windows.Forms.Label()
        Me.btnFiscalSave = New System.Windows.Forms.Button()
        Me.fiscalEnable = New System.Windows.Forms.CheckBox()
        Me.fiscalPassword = New System.Windows.Forms.TextBox()
        Me.fiscalCode = New System.Windows.Forms.TextBox()
        Me.Label4 = New System.Windows.Forms.Label()
        Me.Label3 = New System.Windows.Forms.Label()
        Me.Label1 = New System.Windows.Forms.Label()
        Me.Panel3 = New System.Windows.Forms.Panel()
        Me.btnPOSSave = New System.Windows.Forms.Button()
        Me.posEnable = New System.Windows.Forms.CheckBox()
        Me.TextBox3 = New System.Windows.Forms.TextBox()
        Me.TextBox4 = New System.Windows.Forms.TextBox()
        Me.Label6 = New System.Windows.Forms.Label()
        Me.Label2 = New System.Windows.Forms.Label()
        Me.Label7 = New System.Windows.Forms.Label()
        Me.Button3 = New System.Windows.Forms.Button()
        Me.saveAll = New System.Windows.Forms.Button()
        Me.btnTestFiscal = New System.Windows.Forms.Button()
        Me.Panel2.SuspendLayout()
        Me.Panel3.SuspendLayout()
        Me.SuspendLayout()
        '
        'Panel1
        '
        Me.Panel1.BackColor = System.Drawing.Color.Teal
        Me.Panel1.Location = New System.Drawing.Point(3, 58)
        Me.Panel1.Name = "Panel1"
        Me.Panel1.Size = New System.Drawing.Size(164, 599)
        Me.Panel1.TabIndex = 0
        '
        'Panel2
        '
        Me.Panel2.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle
        Me.Panel2.Controls.Add(Me.btnTestFiscal)
        Me.Panel2.Controls.Add(Me.fiscalDrawer)
        Me.Panel2.Controls.Add(Me.Label8)
        Me.Panel2.Controls.Add(Me.btnFiscalSave)
        Me.Panel2.Controls.Add(Me.fiscalEnable)
        Me.Panel2.Controls.Add(Me.fiscalPassword)
        Me.Panel2.Controls.Add(Me.fiscalCode)
        Me.Panel2.Controls.Add(Me.Label4)
        Me.Panel2.Controls.Add(Me.Label3)
        Me.Panel2.Controls.Add(Me.Label1)
        Me.Panel2.Location = New System.Drawing.Point(173, 58)
        Me.Panel2.Name = "Panel2"
        Me.Panel2.Size = New System.Drawing.Size(427, 249)
        Me.Panel2.TabIndex = 1
        '
        'fiscalDrawer
        '
        Me.fiscalDrawer.Enabled = False
        Me.fiscalDrawer.Location = New System.Drawing.Point(155, 94)
        Me.fiscalDrawer.Name = "fiscalDrawer"
        Me.fiscalDrawer.Size = New System.Drawing.Size(85, 22)
        Me.fiscalDrawer.TabIndex = 8
        '
        'Label8
        '
        Me.Label8.AutoSize = True
        Me.Label8.Location = New System.Drawing.Point(90, 94)
        Me.Label8.Name = "Label8"
        Me.Label8.Size = New System.Drawing.Size(53, 17)
        Me.Label8.TabIndex = 7
        Me.Label8.Text = "Drawer"
        '
        'btnFiscalSave
        '
        Me.btnFiscalSave.Location = New System.Drawing.Point(285, 200)
        Me.btnFiscalSave.Name = "btnFiscalSave"
        Me.btnFiscalSave.Size = New System.Drawing.Size(120, 35)
        Me.btnFiscalSave.TabIndex = 6
        Me.btnFiscalSave.Text = "Save"
        Me.btnFiscalSave.UseVisualStyleBackColor = True
        '
        'fiscalEnable
        '
        Me.fiscalEnable.AutoSize = True
        Me.fiscalEnable.Location = New System.Drawing.Point(93, 122)
        Me.fiscalEnable.Name = "fiscalEnable"
        Me.fiscalEnable.Size = New System.Drawing.Size(82, 21)
        Me.fiscalEnable.TabIndex = 5
        Me.fiscalEnable.Text = "Enabled"
        Me.fiscalEnable.UseVisualStyleBackColor = True
        '
        'fiscalPassword
        '
        Me.fiscalPassword.Enabled = False
        Me.fiscalPassword.Location = New System.Drawing.Point(155, 66)
        Me.fiscalPassword.Name = "fiscalPassword"
        Me.fiscalPassword.Size = New System.Drawing.Size(249, 22)
        Me.fiscalPassword.TabIndex = 4
        '
        'fiscalCode
        '
        Me.fiscalCode.Enabled = False
        Me.fiscalCode.Location = New System.Drawing.Point(155, 38)
        Me.fiscalCode.Name = "fiscalCode"
        Me.fiscalCode.Size = New System.Drawing.Size(249, 22)
        Me.fiscalCode.TabIndex = 3
        '
        'Label4
        '
        Me.Label4.AutoSize = True
        Me.Label4.Location = New System.Drawing.Point(13, 65)
        Me.Label4.Name = "Label4"
        Me.Label4.Size = New System.Drawing.Size(130, 17)
        Me.Label4.TabIndex = 2
        Me.Label4.Text = "Operator Password"
        '
        'Label3
        '
        Me.Label3.AutoSize = True
        Me.Label3.Location = New System.Drawing.Point(13, 38)
        Me.Label3.Name = "Label3"
        Me.Label3.Size = New System.Drawing.Size(102, 17)
        Me.Label3.TabIndex = 1
        Me.Label3.Text = "Operator Code"
        '
        'Label1
        '
        Me.Label1.AutoSize = True
        Me.Label1.Location = New System.Drawing.Point(14, 9)
        Me.Label1.Name = "Label1"
        Me.Label1.Size = New System.Drawing.Size(90, 17)
        Me.Label1.TabIndex = 0
        Me.Label1.Text = "Fiscal Printer"
        '
        'Panel3
        '
        Me.Panel3.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle
        Me.Panel3.Controls.Add(Me.btnPOSSave)
        Me.Panel3.Controls.Add(Me.posEnable)
        Me.Panel3.Controls.Add(Me.TextBox3)
        Me.Panel3.Controls.Add(Me.TextBox4)
        Me.Panel3.Controls.Add(Me.Label6)
        Me.Panel3.Controls.Add(Me.Label2)
        Me.Panel3.Location = New System.Drawing.Point(173, 313)
        Me.Panel3.Name = "Panel3"
        Me.Panel3.Size = New System.Drawing.Size(427, 255)
        Me.Panel3.TabIndex = 2
        '
        'btnPOSSave
        '
        Me.btnPOSSave.Location = New System.Drawing.Point(285, 207)
        Me.btnPOSSave.Name = "btnPOSSave"
        Me.btnPOSSave.Size = New System.Drawing.Size(120, 36)
        Me.btnPOSSave.TabIndex = 11
        Me.btnPOSSave.Text = "Save"
        Me.btnPOSSave.UseVisualStyleBackColor = True
        '
        'posEnable
        '
        Me.posEnable.AutoSize = True
        Me.posEnable.Location = New System.Drawing.Point(323, 121)
        Me.posEnable.Name = "posEnable"
        Me.posEnable.Size = New System.Drawing.Size(82, 21)
        Me.posEnable.TabIndex = 10
        Me.posEnable.Text = "Enabled"
        Me.posEnable.UseVisualStyleBackColor = True
        '
        'TextBox3
        '
        Me.TextBox3.Enabled = False
        Me.TextBox3.Location = New System.Drawing.Point(156, 77)
        Me.TextBox3.Name = "TextBox3"
        Me.TextBox3.Size = New System.Drawing.Size(249, 22)
        Me.TextBox3.TabIndex = 9
        '
        'TextBox4
        '
        Me.TextBox4.Enabled = False
        Me.TextBox4.Location = New System.Drawing.Point(156, 45)
        Me.TextBox4.Name = "TextBox4"
        Me.TextBox4.Size = New System.Drawing.Size(249, 22)
        Me.TextBox4.TabIndex = 8
        '
        'Label6
        '
        Me.Label6.AutoSize = True
        Me.Label6.Location = New System.Drawing.Point(14, 45)
        Me.Label6.Name = "Label6"
        Me.Label6.Size = New System.Drawing.Size(83, 17)
        Me.Label6.TabIndex = 6
        Me.Label6.Text = "Logic Name"
        '
        'Label2
        '
        Me.Label2.AutoSize = True
        Me.Label2.Location = New System.Drawing.Point(14, 9)
        Me.Label2.Name = "Label2"
        Me.Label2.Size = New System.Drawing.Size(83, 17)
        Me.Label2.TabIndex = 1
        Me.Label2.Text = "POS Printer"
        '
        'Label7
        '
        Me.Label7.AutoSize = True
        Me.Label7.Font = New System.Drawing.Font("Microsoft Sans Serif", 13.8!, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Label7.Location = New System.Drawing.Point(170, 9)
        Me.Label7.Name = "Label7"
        Me.Label7.Size = New System.Drawing.Size(177, 29)
        Me.Label7.TabIndex = 3
        Me.Label7.Text = "Printer Set Up"
        '
        'Button3
        '
        Me.Button3.Location = New System.Drawing.Point(458, 608)
        Me.Button3.Name = "Button3"
        Me.Button3.Size = New System.Drawing.Size(120, 35)
        Me.Button3.TabIndex = 12
        Me.Button3.Text = "Close"
        Me.Button3.UseVisualStyleBackColor = True
        '
        'saveAll
        '
        Me.saveAll.Location = New System.Drawing.Point(227, 607)
        Me.saveAll.Name = "saveAll"
        Me.saveAll.Size = New System.Drawing.Size(120, 36)
        Me.saveAll.TabIndex = 13
        Me.saveAll.Text = "Save"
        Me.saveAll.UseVisualStyleBackColor = True
        '
        'btnTestFiscal
        '
        Me.btnTestFiscal.Location = New System.Drawing.Point(3, 200)
        Me.btnTestFiscal.Name = "btnTestFiscal"
        Me.btnTestFiscal.Size = New System.Drawing.Size(120, 35)
        Me.btnTestFiscal.TabIndex = 9
        Me.btnTestFiscal.Text = "Test Connection"
        Me.btnTestFiscal.UseVisualStyleBackColor = True
        '
        'frmPrinters
        '
        Me.AutoScaleDimensions = New System.Drawing.SizeF(8.0!, 16.0!)
        Me.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font
        Me.ClientSize = New System.Drawing.Size(612, 655)
        Me.Controls.Add(Me.saveAll)
        Me.Controls.Add(Me.Button3)
        Me.Controls.Add(Me.Label7)
        Me.Controls.Add(Me.Panel3)
        Me.Controls.Add(Me.Panel2)
        Me.Controls.Add(Me.Panel1)
        Me.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle
        Me.MaximizeBox = False
        Me.MinimizeBox = False
        Me.Name = "frmPrinters"
        Me.ShowIcon = False
        Me.ShowInTaskbar = False
        Me.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent
        Me.Text = "Printer Set up"
        Me.Panel2.ResumeLayout(False)
        Me.Panel2.PerformLayout()
        Me.Panel3.ResumeLayout(False)
        Me.Panel3.PerformLayout()
        Me.ResumeLayout(False)
        Me.PerformLayout()

    End Sub
    Friend WithEvents Panel1 As System.Windows.Forms.Panel
    Friend WithEvents Panel2 As System.Windows.Forms.Panel
    Friend WithEvents fiscalEnable As System.Windows.Forms.CheckBox
    Friend WithEvents fiscalPassword As System.Windows.Forms.TextBox
    Friend WithEvents fiscalCode As System.Windows.Forms.TextBox
    Friend WithEvents Label4 As System.Windows.Forms.Label
    Friend WithEvents Label3 As System.Windows.Forms.Label
    Friend WithEvents Label1 As System.Windows.Forms.Label
    Friend WithEvents Panel3 As System.Windows.Forms.Panel
    Friend WithEvents posEnable As System.Windows.Forms.CheckBox
    Friend WithEvents TextBox3 As System.Windows.Forms.TextBox
    Friend WithEvents TextBox4 As System.Windows.Forms.TextBox
    Friend WithEvents Label6 As System.Windows.Forms.Label
    Friend WithEvents Label2 As System.Windows.Forms.Label
    Friend WithEvents btnFiscalSave As System.Windows.Forms.Button
    Friend WithEvents btnPOSSave As System.Windows.Forms.Button
    Friend WithEvents Label7 As System.Windows.Forms.Label
    Friend WithEvents Button3 As System.Windows.Forms.Button
    Friend WithEvents fiscalDrawer As System.Windows.Forms.TextBox
    Friend WithEvents Label8 As System.Windows.Forms.Label
    Friend WithEvents saveAll As System.Windows.Forms.Button
    Friend WithEvents btnTestFiscal As System.Windows.Forms.Button
End Class
