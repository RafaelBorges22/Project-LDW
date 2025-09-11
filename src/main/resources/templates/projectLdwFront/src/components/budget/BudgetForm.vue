<template>
  <div class="form-container">
    <form class="budget-form">
      <div class="form-row">
        <div class="form-group">
          <label for="description" class="form-label">DESCREVA SUA TATTOO <span class="required">*</span></label>
          <input type="text" id="description" placeholder="Quero tatuar um Dragao cabreiro..." class="form-input" />
        </div>
        <div class="form-group color-option">
          <label for="color" class="form-label">COLORIDA <span class="required">*</span></label>
          <div class="checkbox-wrapper">
            <input type="checkbox" id="color" class="form-checkbox" />
            <span class="checkmark"></span>
          </div>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="body-part" class="form-label">PARTE DO CORPO <span class="required">*</span></label>
          <select id="body-part" class="form-select">
            <option>BRAÇO</option>
            </select>
        </div>
        <div class="form-group">
          <label for="size" class="form-label">TAMANHO <span class="required">*</span></label>
          <select id="size" class="form-select">
            <option>-10CM</option>
            </select>
        </div>
      </div>

      <div class="form-group upload-section">
        <label for="reference-image" class="form-label">IMAGEM DE REFERÊNCIA</label>
        <div class="upload-box">
          <input type="file" id="reference-image" class="file-input" />
          <div class="upload-placeholder">
            <img src="../../../public/images/icons/upload_icon.png" alt="Upload Icon" class="upload-icon" />
            <span>BUSCAR</span>
          </div>
        </div>
      </div>
      
      <button type="submit" class="submit-button">ENVIAR</button>
      
      <p class="form-note">o pedido será enviado para o tatuador, que confirmará disponibilidade e valores.</p>
    </form>
  </div>
</template>

<script>
export default {
  name: 'BudgetForm'
};
</script>

<style scoped>
@import '../../assets/budget/BudgetForm.scss';
</style>