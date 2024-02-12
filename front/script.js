document.getElementById('registrationForm').addEventListener('submit', function(event) {
  event.preventDefault();

  var formData = new FormData(this);

  fetch('http://localhost:5000/register', { // Используем адрес локального сервера
    method: 'POST',
    body: formData
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    console.log('Success:', data);
    // Дополнительные действия после успешной регистрации, например, перенаправление на другую страницу
  })
  .catch(error => {
    console.error('Error:', error);
    // Обработка ошибок
  });
});
