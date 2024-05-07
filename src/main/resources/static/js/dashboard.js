window.changePage = function (page) {
    window.location.href = '/?page=' + page + '&pageSize=' + pageSize + '&sort=' + sort;
}

function openModal(postNo) {
    fetch('/homeModal/' + postNo)
        .then(response => response.json())
        .then(data => {
            const modal = document.getElementById('exampleModal');
            const modalBody = modal.querySelector('#modal-body-content');

            // Clear previous content
            modalBody.innerHTML = '';

            // Single post data
            const post = data.blogPostDto;
            let hashtags = [];
            if (post.hashtag !== null) {
                hashtags = post.hashtag.split(',');
            }

            // Construct new row for the post
            let postRow = `
          <tr>
              <td class="align-middle">
                  <a target="_blank" href="https://blog.naver.com/${post.blogId}/${post.postNo}">
                      <p class="btn btn-link text-dark text-sm font-weight-bold mb-0">${post.postTitle}</p>
                  </a>
                  <div class="post-hashtag">
                      ${hashtags.map(tag => `<div class="hashtag select">#${tag.trim()}</div>`).join('')}
                  </div>
              </td>
              <td class="align-middle text-center">
                  <p class="text-xs font-weight-bold mb-0">${post.postDate}</p>
              </td>
              <td class="align-middle text-center">
                  ${data.keywordDtos.map(keyword => `
                      <a target="_blank" href="https://search.naver.com/search.naver?query=${keyword.keywordName}" style="display: block;" class="btn btn-link text-dark px-3 mb-0">
                          <p class="text-sm font-weight-bold" style="display: inline; padding-right: 5px">${keyword.keywordName}</p>
                          <p class="text-sm font-weight-bold" style="display: inline; color: #00c73c">${keyword.rankPc === 0 ? '-' : '[' + keyword.rankPc + '위]'}</p>
                          <p class="text-sm font-weight-bold" style="display: inline;">
                              [
                              <span class="${keyword.totalSearchExposure === 1 ? 'text-success' : 'text-danger'}">
                                  <i class="${keyword.totalSearchExposure === 1 ? 'fa fa-check' : 'fa fa-times'}"></i>
                              </span>
                              ]
                          </p>
                      </a>
                  `).join('')}
              </td>
          </tr>
      `;

            modalBody.innerHTML = postRow;

            // Show the modal
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}